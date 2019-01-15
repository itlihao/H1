package com.zhiyihealth.registration.lib_user.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiyihealth.registration.lib_base.data.CacheDataSource;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfo;
import com.zhiyihealth.registration.lib_base.entity.EmnuLeft;
import com.zhiyihealth.registration.lib_base.view.MDDialog;
import com.zhiyihealth.registration.lib_user.LoginActivity;
import com.zhiyihealth.registration.lib_user.R;
import com.zhiyihealth.registration.lib_user.adapter.DrawAdapter;
import com.zhiyihealth.registration.lib_user.bean.MenuItem;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Lihao
 * @date 2019-1-10
 * Email heaolihao@163.com
 */
public class FragmentMenu extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static MDDialog mDialog;

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
        for (DoctorInfo doctorInfo : clinicDoctorInfo) {
            if (doctorInfo.getSysUserId().equals(CacheDataSource.getDoctorMainId())) {
                if ("1".equals(doctorInfo.getSex())) {
                    mUserHead.setImageResource(R.drawable.ic_head_man);
                } else {
                    mUserHead.setImageResource(R.drawable.ic_head_women);
                }
                break;
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
        LinearLayoutManager gridLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mMenuView.setLayoutManager(gridLayoutManager1);
        mMenuView.setAdapter(drawAdapter);
    }

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
                    /*CC.obtainBuilder(Components.ComponentAppInit)
                            .setActionName(Components.ComponentAppjpush)
                            .build().call();*/
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton(v -> mDialog.dismiss())
                .create();
        mDialog.show();
    }
}
