package com.hospital.s1m.lib_user.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.data.CacheDataSource;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.EmnuLeft;
import com.hospital.s1m.lib_base.view.MDDialog;
import com.hospital.s1m.lib_user.R;
import com.hospital.s1m.lib_user.adapter.DrawAdapter;
import com.hospital.s1m.lib_user.bean.MenuItem;
import com.hospital.s1m.lib_user.ui.LoginActivity;

import java.util.ArrayList;
import java.util.Objects;

import static com.hospital.s1m.lib_user.adapter.DrawAdapter.MODEL_QUICK;

/**
 * @author Lihao
 * @date 2019-1-10
 * Email heaolihao@163.com
 */
public class FragmentMenu extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static MDDialog mDialog;
    private boolean mIsChecked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.user_menu_fragment, null);
        initView(view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initView(View view) {
        ImageView mUserHead = view.findViewById(R.id.user_haedimg);
        TextView mUserName = view.findViewById(R.id.user_name);
        TextView mClinic = view.findViewById(R.id.clinic);

        RecyclerView mMenuView = view.findViewById(R.id.drawer_rlv);

        if (CacheDataSource.getClinicName().length() > 12) {
            mClinic.setText(CacheDataSource.getClinicName().substring(0, 12) + "...");
        } else {
            mClinic.setText(CacheDataSource.getClinicName());
        }
        mUserName.setText(CacheDataSource.getUserName());

        ArrayList<DoctorInfo> clinicDoctorInfo = CacheDataSource.getClinicDoctorInfo();
        if (clinicDoctorInfo != null) {
            for (DoctorInfo doctorInfo : clinicDoctorInfo) {
                if (doctorInfo.getDoctorId().equals(CacheDataSource.getDoctorMainId())) {
                    if ("1".equals(doctorInfo.getSex())) {
                        mUserHead.setImageResource(R.drawable.ic_head_man);
                    } else {
                        mUserHead.setImageResource(R.drawable.ic_head_women);
                    }
                    break;
                }
            }
        }

        ArrayList menuItem = MenuItem.getEmnu();
        DrawAdapter drawAdapter = new DrawAdapter(getActivity(), menuItem);

        drawAdapter.setOnItemClickListener((adapter, view1, position) -> {
            int itemViewType = adapter.getItemViewType(position);
            if (itemViewType == EmnuLeft.ITEM_TITLE) {
                EmnuLeft emnuLeft = (EmnuLeft) menuItem.get(position);
                String key = emnuLeft.getKey();
                if (key.equals(MenuItem.QUIT_SYSTEM)) {
                    quitAcount();
                }
            }
        });
        drawAdapter.setCheckedChangedListener((view12, isChecked) -> {
            mIsChecked = isChecked;
            if (isChecked) {
                // 跳转到快速挂号页面
                CC.obtainBuilder(Components.COMPONENT_APP_MAIN)
                        .setActionName(Components.COMPONENT_APP_JUMP)
                        .build()
                        .callAsyncCallbackOnMainThread(mStartCallback);
            } else {
                // 跳转到挂号页面
                CC.obtainBuilder(Components.COMPONENT_APP_MAIN)
                        .setActionName(Components.COMPONENT_APP_JUMPN)
                        .build()
                        .callAsyncCallbackOnMainThread(mStartCallback);

            }
        });
        LinearLayoutManager gridLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mMenuView.setLayoutManager(gridLayoutManager1);
        mMenuView.setAdapter(drawAdapter);
    }

    IComponentCallback mStartCallback = (cc, result) -> {
        if (result.isSuccess()) {
            SPDataSource.put(Objects.requireNonNull(getActivity()), MODEL_QUICK, mIsChecked);
            getActivity().finish();
        }
    };

    private void quitAcount() {
        mDialog = new MDDialog.Builder(getContext())
                .setShowTitle(false)
                .setShowMessage(true)
                .setMessages(Objects.requireNonNull(getContext()).getString(R.string.sr_quit))
                .setShowAvi(false)
                .setWidthMaxDp(440)
                .setShowNegativeButton(true)
                .setShowPositiveButton(true)
                .setCancelable(false)
                .setPositiveButton(v -> {
                    CacheDataSource.clearCache();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton(v -> mDialog.dismiss())
                .create();
        mDialog.show();
    }
}
