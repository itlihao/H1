package com.zhiyihealth.registration.lib_base.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhiyihealth.registration.lib_base.contract.MainContract;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfo;
import com.zhiyihealth.registration.lib_base.entity.QuickRegistr;
import com.zhiyihealth.registration.lib_base.listener.ResponseListener;
import com.zhiyihealth.registration.lib_base.model.RegistrationModel;

import java.util.ArrayList;

/**
 * @author Lihao
 * @date 2019-2-14
 * Email heaolihao@163.com
 */
public class RegistrationPresenter {
    private MainContract.RegistrationView mView;

    private RegistrationModel mModel;

    private Context mContext;

    public RegistrationPresenter(Context context, RegistrationModel registrationModel, MainContract.RegistrationView regostrationView) {
        this.mContext = context;
        this.mModel = registrationModel;
        this.mView = regostrationView;
    }

    // 快速挂号
    public void quickRegistration(String clinicId, String sysUserId) {
        mView.showLoading("挂号中");
        mModel.quickRegistration(mContext, clinicId, sysUserId, new ResponseListener<QuickRegistr>() {
            @Override
            public QuickRegistr convert(String jsonStr) {
                return JSON.parseObject(jsonStr, QuickRegistr.class);
            }

            @Override
            public void onSuccess(QuickRegistr result) {
                if (mView != null) {
                    mView.hideLoading();
                    mView.onQuickRegistration(result);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    mView.hideLoading();
                }
            }
        });
    }

    /**
     * 获取医生列表
     */
    public void getDoctorInfo(String clinicId) {
        mModel.getEmployList(mContext, clinicId, new ResponseListener<ArrayList<DoctorInfo>>() {
            @Override
            public ArrayList<DoctorInfo> convert(String jsonStr) {
                return JSON.parseObject(jsonStr, new TypeReference<ArrayList<DoctorInfo>>() {
                });
            }

            @Override
            public void onSuccess(ArrayList<DoctorInfo> result) {
                if (mView != null) {
                    mView.hideLoading();
                    //2.保存医生信息
                    mModel.saveSystemData(result);
                    mView.onDoctorResult(result);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    mView.hideLoading();
                }
            }
        });
    }
}
