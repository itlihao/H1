package com.hospital.s1m;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.hospital.s1m.adapter.DoctorItemAdapter;
import com.hospital.s1m.adapter.SpacesItemDecoration;
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
import com.hospital.s1m.lib_base.listener.CallInterface;
import com.hospital.s1m.lib_base.model.RegistrationModel;
import com.hospital.s1m.lib_base.presenter.RegistrationPresenter;
import com.hospital.s1m.lib_base.utils.DeviceInfoUtils;
import com.hospital.s1m.lib_base.utils.Logger;
import com.hospital.s1m.lib_base.utils.NetworkUtils;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_base.utils.Utils;
import com.hospital.s1m.lib_base.view.MDDialog;
import com.jakewharton.rxbinding2.view.RxView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lihao
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener, MainContract.RegistrationView {

    private FrameLayout mDwMenu;
    private DrawerLayout mDrawerLayout;

    private RecyclerView doctorView;
    private DoctorItemAdapter doctorAdapter;
    private MDDialog mFullDialog;

    private RelativeLayout mNetState;

    private Button mBtnGet;
    private SwipeRefreshLayout mSwiperefreshlayout;

    private ArrayList<DoctorInfoCheck> mDoctorList;

    private int resId;

    private RegistrationPresenter mPresenter;

    private String sysUserId;

    private String periodType = null;

    private boolean isRegistration = false;
    private BaseActivity mActivity;

    private MHandler mhandler = new MHandler();

    @SuppressLint("HandlerLeak")
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    isRegistration = false;
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    break;
                case 0:
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

        mPresenter = new RegistrationPresenter(RegistrationActivity.this, new RegistrationModel(), RegistrationActivity.this);
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 0);
        mActivity = this;
        initData();
        initAdapter();
        onListener();

        EventBus.getDefault().register(this);
        String sn = DeviceInfoUtils.getDeviceSN();
        Logger.d("RegistrationActivity", "[sn]: " + sn);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkNet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            // 反注册EventBus
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideLoading();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_registration;
    }

    @SuppressLint({"SetTextI18n", "CheckResult"})
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

        mSwiperefreshlayout = findViewById(R.id.swiperefreshlayout);
        mSwiperefreshlayout.setColorSchemeColors(Color.rgb(47, 223, 189));

        /*mIvMenu.setOnClickListener(this);
        mBtnGet.setOnClickListener(this);
        mNetText.setOnClickListener(this);
        mNetClose.setOnClickListener(this);*/

        RxView.clicks(mBtnGet)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if (!isRegistration) {
                        isRegistration = true;
                        mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId, periodType);
                    } else {
                        ToastUtils.showToast(RegistrationActivity.this, "请勿重复点击");
                    }
                });

        RxView.clicks(mIvMenu)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    //cc调用lib_user侧滑界面
                    mDrawerLayout.openDrawer(mDwMenu);
                    CC.obtainBuilder(Components.COMPONENT_USER)
                            .setActionName(Components.COMPONENT_USER_MENU)
                            .build()
                            .callAsyncCallbackOnMainThread(fragmentdrawer);
                });

        RxView.clicks(mNetText)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                });

        RxView.clicks(mNetClose)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    mNetState.setVisibility(View.GONE);
                });

        mSwiperefreshlayout.setOnRefreshListener(this::refresh);

        if (CacheDataSource.getClinicName().length() > 12) {
            mWelcome.setText(CacheDataSource.getClinicName().substring(0, 12) + "..." + getString(R.string.sr_message));
        } else {
            mWelcome.setText(CacheDataSource.getClinicName() + getString(R.string.sr_message));
        }
        mDoctorList = new ArrayList<>();
    }


    private void initData() {
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        resId = R.layout.item_doctor_line;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        SpacesItemDecoration itemDecoration = new SpacesItemDecoration(18);
        doctorView.setLayoutManager(linearLayoutManager);
        doctorView.addItemDecoration(itemDecoration);
    }

    private void initAdapter() {
        doctorAdapter = new DoctorItemAdapter(this, resId, mDoctorList);
        doctorView.setAdapter(doctorAdapter);
        doctorAdapter.setCallInterface(itemClick);
    }

    @SuppressLint("CheckResult")
    private void onListener() {
        RxView.drags(mDrawerLayout).subscribe(dragEvent -> {
            //动态添加fragment进来
            CC.obtainBuilder(Components.COMPONENT_USER)
                    .setActionName(Components.COMPONENT_USER_MENU)
                    .build()
                    .callAsyncCallbackOnMainThread(fragmentdrawer);
        });
    }

    private CallInterface itemClick = new CallInterface() {
        @Override
        public void getNum(String sysUserId, boolean isFulled) {
            if (!"T117181700815".equals(DeviceInfoUtils.getDeviceSN())) {
                ToastUtils.showToast(RegistrationActivity.this, "该诊所暂不提供快速挂号服务，请联系客服！");
                return;
            }

            if (isFulled) {
                return;
            }

            if (!Utils.isFastClick() && !isRegistration) {
                isRegistration = true;
                mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId, periodType);
            } else {
                ToastUtils.showToast(RegistrationActivity.this, "请勿重复点击");
            }
        }
    };

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
                Logger.w("Registration", "isRegistration: " + isRegistration);
                if (!Utils.isFastClick() && !isRegistration) {
                    isRegistration = true;
                    mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId, periodType);
                } else {
                    ToastUtils.showToast(this, "请勿重复点击");
                }
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

    public void refresh() {
        mDoctorList.clear();
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void regChageEventBus(String msg) {
        Logger.d("BaseActivity", "[JPushReceiver] 接收到推送下来的: " + msg);
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onQuickRegistration(RegistrCall quickRegistr) {
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
        sysUserId = "";
        periodType = null;

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
                    .addParam("wait", quickRegistr.getWaitNum())
                    .addParam("activity", mActivity)
                    .addParam("mainThreadHandler", MyApplication.getMainThreadHandler())
                    .build()
                    .callAsyncCallbackOnMainThread((cc, result) -> {
                                isRegistration = false;
                                hideLoading();
                            }
                    );
        } else {
            mDialog = new MDDialog.Builder(RegistrationActivity.this)
                    .setShowTitle(false)
                    .setShowMessage(true)
                    .setMessages(Objects.requireNonNull(RegistrationActivity.this).getString(com.hospital.s1m.lib_user.R.string.sr_registration_suc))
                    .setShowAvi(false)
                    .setshowRNo(true)
                    .setRNo(String.format("%03d", quickRegistr.getRegistrationNo()) + "号")
                    .setWidthMaxDp(440)
                    .setShowNegativeButton(false)
                    .setShowPositiveButton(false)
                    .setCancelable(false)
                    .create();
            mDialog.show();
            mhandler.sendEmptyMessageDelayed(1, 1500);
        }
//        mhandler.postDelayed(() -> isRegistration = false, 1550);

    }

    @Override
    public void onDoctorResult(ArrayList<DoctorInfo> result) {
        mSwiperefreshlayout.setRefreshing(false);
        if (result != null) {
//            initData();
//            initAdapter();
            mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
            doctorAdapter.setNewData(mDoctorList);
            doctorAdapter.notifyDataSetChanged();

            if (mDoctorList.size() == 0) {
                View headView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
                doctorAdapter.setEmptyView(headView);
            }
        }

        hideLoading();

    }

    @Override
    public void onPatientResult(List<Patient> result) {

    }

    @Override
    public void onPeriodFullResult(String result, String type) {
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
                    mFullDialog.dismiss();
                    periodType = type;
                    mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId, periodType);
                })
                .setNegativeButton(v -> {
                    mFullDialog.dismiss();
                    sysUserId = "";
                    periodType = null;
                    isRegistration = false;
                })
                .create();
        mFullDialog.show();
    }

    @Override
    public void onFailed() {
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
        sysUserId = "";
        periodType = null;
        isRegistration = false;
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
