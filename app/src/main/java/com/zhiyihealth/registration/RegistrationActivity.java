package com.zhiyihealth.registration;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiyihealth.registration.adapter.DoctorItemAdapter;
import com.zhiyihealth.registration.lib_base.base.BaseActivity;
import com.zhiyihealth.registration.lib_base.constants.Components;
import com.zhiyihealth.registration.lib_base.data.CacheDataSource;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfoCheck;
import com.zhiyihealth.registration.lib_base.view.MyGridLayoutManager;

import java.util.ArrayList;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mIvMenu;

    private Button mBtnGet;

    private FrameLayout mDwMenu;
    private Fragment mDwFragment;
    private DrawerLayout mDrawerLayout;

    private TextView mWelcome;

    private RecyclerView doctorView;

    private DoctorItemAdapter doctorAdapter;

    private ArrayList<DoctorInfoCheck> mDoctorList;

    private int NUM = 6;

    private int resId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        onListener();
        initData();
        initAdapter();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_registration;
    }

    private void initView() {
        mIvMenu = findViewById(R.id.iv_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDwMenu = findViewById(R.id.dw_fragment);
        doctorView = findViewById(R.id.rv_doctor_item);
        mWelcome = findViewById(R.id.tv_welcome);

        mBtnGet = findViewById(R.id.btn_getNum);

        mIvMenu.setOnClickListener(this);
        mBtnGet.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        if (CacheDataSource.getClinicName().length() > 12) {
            mWelcome.setText(CacheDataSource.getClinicName().substring(0, 12) + "..." + getString(R.string.sr_message));
        } else {
            mWelcome.setText(CacheDataSource.getClinicName() + getString(R.string.sr_message));
        }
        mDoctorList = new ArrayList<>();
        for (int i = 0; i < NUM; i++) {
            DoctorInfoCheck info = new DoctorInfoCheck();
            info.setRealName("张三" + i);
            info.setSex(i % 2 == 0 ? "1" : "2");
            if (i == 4) {
                info.setFulled(true);
            }
            mDoctorList.add(info);
        }

        if (mDoctorList.size() <= 2) {
            resId = R.layout.item_doctor_high_wide;
        } else if (mDoctorList.size() == 3) {
            resId = R.layout.item_doctor_low_wide;
        } else if (mDoctorList.size() == 4) {
            resId = R.layout.item_doctor_high_narrow;
        } else {
            resId = R.layout.item_doctor_low_narrow;
        }

        if (mDoctorList.size() <= 3) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            doctorView.setLayoutManager(linearLayoutManager);
        } else {
            MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(this, 2);
            gridLayoutManager.setScrollEnabled(false);
            doctorView.setLayoutManager(gridLayoutManager);
        }
    }

    private void initAdapter() {

        doctorAdapter = new DoctorItemAdapter(this, resId, mDoctorList);
        doctorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DoctorInfoCheck infoCheck = (DoctorInfoCheck) adapter.getItem(position);
                assert infoCheck != null;
                if (infoCheck.isFulled()) {
                    return;
                }
                setCheck(adapter, position);
            }
        });
        doctorView.setAdapter(doctorAdapter);
    }

    private void onListener() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //动态添加fragment进来
                CC.obtainBuilder(Components.COMPONENT_USER)
                        .setActionName(Components.COMPONENT_USER_MENU)
                        .build()
                        .callAsyncCallbackOnMainThread(fragmentdrawer);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void setCheck(BaseQuickAdapter adapter, int position) {
        if (adapter instanceof DoctorItemAdapter) {
            DoctorInfoCheck item = (DoctorInfoCheck) adapter.getItem(position);
            DoctorItemAdapter adapter1 = (DoctorItemAdapter) adapter;
            int preCheck = adapter1.getPreCheck();
            if (preCheck >= 0) {
                DoctorInfoCheck item1 = adapter1.getItem(preCheck);
                item1.setCheck(false);
            }
            item.setCheck(true);
            adapter1.setPreCheck(position);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                //cc调用lib_user侧滑界面
                mDrawerLayout.openDrawer(mDwMenu);
                CC.obtainBuilder(Components.COMPONENT_USER)
                        .setActionName(Components.COMPONENT_USER_MENU)
                        .build()
                        .callAsyncCallbackOnMainThread(fragmentdrawer);
                break;

            case R.id.btn_getNum:
                NUM++;
                if (NUM > 6) {
                    NUM = 1;
                }
                mDoctorList.clear();
                initData();
                initAdapter();
                break;

            default:
                break;
        }
    }

    IComponentCallback fragmentdrawer = (cc, result) -> {
        if (result.isSuccess()) {
            Fragment fragment = result.getDataItem("Fragment_Jurisdiction");
            if (fragment != null) {
                showdwFragment(fragment);
            }
        }
    };

    private void showdwFragment(Fragment mDwFragment) {
        if (mDwFragment != null) {
            this.mDwFragment = mDwFragment;
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.dw_fragment, mDwFragment);
            trans.commit();
        }
    }
}
