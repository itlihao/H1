package com.hospital.s1m;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hospital.s1m.adapter.DoctorItemAdapter;
import com.hospital.s1m.lib_base.base.BaseActivity;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.constants.Formatter;
import com.hospital.s1m.lib_base.contract.MainContract;
import com.hospital.s1m.lib_base.data.CacheDataSource;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.DoctorInfoCheck;
import com.hospital.s1m.lib_base.entity.Patient;
import com.hospital.s1m.lib_base.entity.RegistrCall;
import com.hospital.s1m.lib_base.model.RegistrationModel;
import com.hospital.s1m.lib_base.presenter.RegistrationPresenter;
import com.hospital.s1m.lib_base.utils.LiveDataBus;
import com.hospital.s1m.lib_base.utils.Logger;
import com.hospital.s1m.lib_base.utils.NetworkUtils;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_base.view.MDDialog;
import com.hospital.s1m.lib_base.view.MyGridLayoutManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Lihao
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener, MainContract.RegistrationView {

    private FrameLayout mDwMenu;
    private DrawerLayout mDrawerLayout;

    private RecyclerView doctorView;
    private MDDialog mDialog;
    private MDDialog mFullDialog;

    private RelativeLayout mNetState;

    private Button mBtnGet;

    private ArrayList<DoctorInfoCheck> mDoctorList;

    private int resId;

    private RegistrationPresenter mPresenter;

    private String sysUserId;

    private String next = null;

    private MHandler mhandler = new MHandler();

    @SuppressLint("HandlerLeak")
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        mPresenter = new RegistrationPresenter(RegistrationActivity.this, new RegistrationModel(), this);
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 0);
        initData();
        initAdapter();
        onListener();

        LiveDataBus.get().with("jpushMessage", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String info) {
                        Logger.d("BaseActivity", "[JPushReceiver] 接收到推送下来的: " + info);
                        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (mPresenter != null) {
            mPresenter.getDoctorInfo(CacheDataSource.getClinicId());
        }*/

        checkNet();
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

        mBtnGet = findViewById(R.id.btn_getNum);

        mNetState = findViewById(R.id.rl_netbar);
        TextView mNetText = findViewById(R.id.tv_net_state);
        ImageView mNetClose = findViewById(R.id.iv_close);

        mIvMenu.setOnClickListener(this);
        mBtnGet.setOnClickListener(this);
        mNetText.setOnClickListener(this);
        mNetClose.setOnClickListener(this);

        if (CacheDataSource.getClinicName().length() > 12) {
            mWelcome.setText(CacheDataSource.getClinicName().substring(0, 12) + "..." + getString(R.string.sr_message));
        } else {
            mWelcome.setText(CacheDataSource.getClinicName() + getString(R.string.sr_message));
        }
        mDoctorList = new ArrayList<>();
    }


    private void initData() {
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
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
                mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId, next);
                break;
            case R.id.tv_net_state:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                break;
            case R.id.iv_close:
                mNetState.setVisibility(View.GONE);
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onQuickRegistration(RegistrCall quickRegistr) {
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
        sysUserId = "";
        next = null;

        boolean print = (boolean) SPDataSource.get(this, "needPrint", true);
        if (print) {
            showLoading("挂号成功，正在为您打印挂号单");
            List<RegistrCall.TimeTableBean> timeTable = quickRegistr.getTimeTable();
            String s1 = "", s2 = "", s3 = "";
            for (int i = 0; i < timeTable.size(); i++) {
                if (timeTable.get(i).getPeriodType() == 1) {
                    s1 = "上午 " + timeTable.get(i).getStartTime() + "-" + timeTable.get(i).getEndTime();
                } else if (timeTable.get(i).getPeriodType() == 2) {
                    s2 = "下午 " + timeTable.get(i).getStartTime() + "-" + timeTable.get(i).getEndTime();
                } else if (timeTable.get(i).getPeriodType() == 3) {
                    s3 = "晚上 " + timeTable.get(i).getStartTime() + "-" + timeTable.get(i).getEndTime();
                }
            }
            CC.obtainBuilder(Components.ComponentPrint)
                    .setActionName(Components.ComponentPrintNumber)
                    .addParam("number", quickRegistr.getRegistrationNo())
                    .addParam("doctorName", quickRegistr.getDoctorName())
                    .addParam("registerDate", Formatter.DATE_FORMAT4.format(new Date()))
                    .addParam("registrationId", quickRegistr.getRegistrationId())
                    .addParam("sysUserId", quickRegistr.getSysUserId())
                    .addParam("periodType", quickRegistr.getPeriodType())
                    .addParam("registerType", 1)
                    .addParam("timeS1", s1)
                    .addParam("timeS2", s2)
                    .addParam("timeS3", s3)
                    .build()
                    .callAsyncCallbackOnMainThread((cc, result) -> {
                        if (result.isSuccess()) {
                            hideLoading();
                        }
                    });
        } else {
            mDialog = new MDDialog.Builder(this)
                    .setShowTitle(false)
                    .setShowMessage(true)
                    .setMessages(Objects.requireNonNull(this).getString(com.hospital.s1m.lib_user.R.string.sr_registration_suc))
                    .setShowAvi(false)
                    .setshowRNo(true)
                    .setRNo(String.format("%02d", quickRegistr.getRegistrationNo()) + "号")
                    .setWidthMaxDp(440)
                    .setShowNegativeButton(false)
                    .setShowPositiveButton(false)
                    .setCancelable(false)
                    .create();
            mDialog.show();
            mhandler.sendEmptyMessageDelayed(1, 1500);
        }

    }

    @Override
    public void onDoctorResult(ArrayList<DoctorInfo> result) {
        mDoctorList.clear();
        initData();
        initAdapter();

        hideLoading();
        if (mDoctorList.size() < 1) {
            mBtnGet.setClickable(false);
        } else {
            mBtnGet.setClickable(true);
        }
    }

    @Override
    public void onPatientResult(List<Patient> result) {

    }

    @Override
    public void onPeriodFullResult(String result) {
        mFullDialog = new MDDialog.Builder(this)
                .setShowTitle(false)
                .setShowMessage(true)
                .setMessages(result)
                .setShowAvi(false)
                .setWidthMaxDp(440)
                .setShowNegativeButton(true)
                .setShowPositiveButton(true)
                .setCancelable(false)
                .setPositiveButton(v -> {
                    // TODO 继续挂号
                    next = "confirm";
                    mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId, next);
                })
                .setNegativeButton(v -> mFullDialog.dismiss())
                .create();
        mFullDialog.show();
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onChangeListener(int netMobile) {
        netType = netMobile;
        if (!isNetConnect()) {
            mNetState.setVisibility(View.VISIBLE);
        } else {
            mNetState.setVisibility(View.GONE);
        }
    }

    @Override
    public void checkNet() {
        netType = NetworkUtils.getNetWorkState(this);
        if (!isNetConnect()) {
            mNetState.setVisibility(View.VISIBLE);
        } else {
            mNetState.setVisibility(View.GONE);
        }
        isNetConnect();
    }
}
