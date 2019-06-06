package com.hospital.s1m.ui;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.billy.cc.core.component.CC;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hospital.s1m.R;
import com.hospital.s1m.adapter.DoctorSelectorItemListAdapter;
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
import com.hospital.s1m.lib_base.utils.Logger;
import com.hospital.s1m.lib_base.utils.Utils;
import com.hospital.s1m.lib_base.view.MDDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class RegistrationActivity extends BaseActivity implements View.OnClickListener, MainContract.RegistrationView {
    private RecyclerView doctorView;
    private Button mRegistration;
    private ArrayList<DoctorInfoCheck> mDoctorList;

    private RegistrationPresenter mPresenter;

    private String sysUserId;
    private String periodType = null;
    private boolean isRegistration = false;
    private DoctorSelectorItemListAdapter doctorAdapter;

    private MDDialog mDialog;
    private MDDialog mFullDialog;

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

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (mPresenter != null) {
            mPresenter.getDoctorInfo(CacheDataSource.getClinicId());
        }*/
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
    protected int setContentView() {
        return R.layout.activity_registration;
    }

    private void initView() {
        doctorView = findViewById(R.id.rv_doctor_list);

        mRegistration = findViewById(R.id.select_doctor);
        mRegistration.setOnClickListener(this);

        mDoctorList = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        doctorView.setLayoutManager(linearLayoutManager);

//        doctorView.addItemDecoration(new LinearItemDecoration(0, 2, 0, 0));
    }

    private void initAdapter() {
        doctorAdapter = new DoctorSelectorItemListAdapter(this, mDoctorList);
        doctorAdapter.setOnItemClickListener((adapter, view, position) -> {
            DoctorInfoCheck infoCheck = (DoctorInfoCheck) adapter.getItem(position);
            assert infoCheck != null;
            sysUserId = infoCheck.getDoctorId();
            if (infoCheck.isFulled()) {
                return;
            }
            setCheck(adapter, position);
        });
        doctorView.setAdapter(doctorAdapter);
    }

    private void setCheck(BaseQuickAdapter adapter, int position) {
        if (adapter instanceof DoctorSelectorItemListAdapter) {
            DoctorInfoCheck item = (DoctorInfoCheck) adapter.getItem(position);
            DoctorSelectorItemListAdapter adapter1 = (DoctorSelectorItemListAdapter) adapter;
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
            case R.id.select_doctor:

                if (!Utils.isFastClick() && !isRegistration) {
                    isRegistration = true;
                    mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId, periodType);
                }
                break;
            default:
                break;
        }
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
        isRegistration = false;

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
        hideLoading();
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        doctorAdapter.setNewData(mDoctorList);
        doctorAdapter.notifyDataSetChanged();
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
        sysUserId = "";
        periodType = null;
        isRegistration = false;
    }
}
