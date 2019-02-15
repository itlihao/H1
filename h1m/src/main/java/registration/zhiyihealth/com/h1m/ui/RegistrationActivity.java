package registration.zhiyihealth.com.h1m.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.billy.cc.core.component.CC;
import com.chad.library.adapter.base.BaseQuickAdapter;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import registration.zhiyihealth.com.h1m.R;
import registration.zhiyihealth.com.h1m.adapter.DoctorSelectorItemListAdapter;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener, MainContract.RegistrationView {
    private RecyclerView doctorView;
    private Button mRegistration;
    private ArrayList<DoctorInfoCheck> mDoctorList;

    private RegistrationPresenter mPresenter;

    private String sysUserId;
    private DoctorSelectorItemListAdapter doctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        mPresenter = new RegistrationPresenter(RegistrationActivity.this, new RegistrationModel(), this);
        initData();
        initAdapter();
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
                mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId);
                break;
            default:
                break;
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
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        doctorAdapter.setNewData(mDoctorList);
        doctorAdapter.notifyDataSetChanged();
    }
}
