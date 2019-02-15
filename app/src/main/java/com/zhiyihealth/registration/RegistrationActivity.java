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
import com.zhiyihealth.registration.lib_base.constants.Formatter;
import com.zhiyihealth.registration.lib_base.contract.MainContract;
import com.zhiyihealth.registration.lib_base.data.CacheDataSource;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfo;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfoCheck;
import com.zhiyihealth.registration.lib_base.entity.QuickRegistr;
import com.zhiyihealth.registration.lib_base.model.RegistrationModel;
import com.zhiyihealth.registration.lib_base.presenter.RegistrationPresenter;
import com.zhiyihealth.registration.lib_base.utils.ToastUtils;
import com.zhiyihealth.registration.lib_base.view.MyGridLayoutManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * @author Lihao
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener, MainContract.RegistrationView {

    private FrameLayout mDwMenu;
    private DrawerLayout mDrawerLayout;

    private RecyclerView doctorView;

    private ArrayList<DoctorInfoCheck> mDoctorList;

    private int resId;

    private RegistrationPresenter mPresenter;

    private String sysUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        mPresenter = new RegistrationPresenter(RegistrationActivity.this, new RegistrationModel(), this);
        initData();
        initAdapter();
        onListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.getDoctorInfo(CacheDataSource.getClinicId());
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_registration;
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        ImageView mIvMenu = findViewById(R.id.iv_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDwMenu = findViewById(R.id.dw_fragment);
        doctorView = findViewById(R.id.rv_doctor_item);
        TextView mWelcome = findViewById(R.id.tv_welcome);

        Button mBtnGet = findViewById(R.id.btn_getNum);

        mIvMenu.setOnClickListener(this);
        mBtnGet.setOnClickListener(this);

        if (CacheDataSource.getClinicName().length() > 12) {
            mWelcome.setText(CacheDataSource.getClinicName().substring(0, 12) + "..." + getString(R.string.sr_message));
        } else {
            mWelcome.setText(CacheDataSource.getClinicName() + getString(R.string.sr_message));
        }
        mDoctorList = new ArrayList<>();
    }


    private void initData() {
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        /*for (DoctorInfo doctorInfo : doctorlist) {
            String initial = getLetter(doctorInfo.getRealName());
            doctorInfo.setInitial(initial);
        }
        sort(doctorlist);*/
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
        DoctorItemAdapter doctorAdapter = new DoctorItemAdapter(this, resId, mDoctorList);
        doctorView.setAdapter(doctorAdapter);
        doctorAdapter.setOnItemClickListener((adapter, view, position) -> {
            DoctorInfoCheck infoCheck = (DoctorInfoCheck) adapter.getItem(position);
            assert infoCheck != null;
            sysUserId = infoCheck.getDoctorId();
            if (infoCheck.isFulled()) {
                return;
            }
            setCheck(adapter, position);
        });
    }

    private void onListener() {
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
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
                Objects.requireNonNull(item1).setCheck(false);
            }
            Objects.requireNonNull(item).setCheck(true);
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
                mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId);
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
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.dw_fragment, mDwFragment);
            trans.commit();
        }
    }

    @Override
    public void onQuickRegistration(QuickRegistr quickRegistr) {
        showLoading("挂号成功，正在为您打印挂号单");
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId());
        sysUserId = "";
        CC.obtainBuilder(Components.ComponentPrint)
                .setActionName(Components.ComponentPrintNumber)
                .addParam("number", quickRegistr.getRegistrationNo())
                .addParam("doctorName", quickRegistr.getDoctorName())
                .addParam("registerDate", Formatter.DATE_FORMAT4.format(new Date()))
                .addParam("registrationId", quickRegistr.getRegistrationId())
                .addParam("sysUserId", quickRegistr.getSysUserId())
                .addParam("periodType", quickRegistr.getPeriodType())
                .build()
                .callAsyncCallbackOnMainThread((cc, result) -> {
                    if (result.isSuccess()) {
                        ToastUtils.showToast(RegistrationActivity.this, "打印完毕");
                        hideLoading();
                    }
                });
    }

    @Override
    public void onDoctorResult(ArrayList<DoctorInfo> result) {
        mDoctorList.clear();
        initData();
        initAdapter();
    }
}
