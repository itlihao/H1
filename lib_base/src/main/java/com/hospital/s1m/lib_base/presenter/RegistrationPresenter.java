package com.hospital.s1m.lib_base.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hospital.s1m.lib_base.contract.MainContract;
import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.Patient;
import com.hospital.s1m.lib_base.entity.PatientAndRegistrationParmar;
import com.hospital.s1m.lib_base.entity.RegistrCall;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.hospital.s1m.lib_base.model.RegistrationModel;
import com.hospital.s1m.lib_base.utils.CodeUtils;
import com.hospital.s1m.lib_base.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

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
    public void quickRegistration(String clinicId, String sysUserId, String next) {
        mView.showLoading("挂号中");
        mModel.quickRegistration(mContext, clinicId, sysUserId, next, new ResponseListener<RegistrCall>() {
            @Override
            public RegistrCall convert(String jsonStr) {
                return JSON.parseObject(jsonStr, RegistrCall.class);
            }

            @Override
            public void onSuccess(RegistrCall result) {
                if (mView != null) {
                    mView.hideLoading();
                    mView.onQuickRegistration(result);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    if ("2020106".equals(errorCode)) {
                        mView.onPeriodFullResult(errorInfo);
                    } else {
                        ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode));
                    }
                    mView.hideLoading();
                }
            }
        });
    }

    // 普通挂号
    public void saveAndRegistration(PatientAndRegistrationParmar info) {
        mView.showLoading("挂号中");
        mModel.saveAndRegistration(mContext, info, new ResponseListener<RegistrCall>() {
            @Override
            public RegistrCall convert(String jsonStr) {
                return JSON.parseObject(jsonStr, RegistrCall.class);
            }

            @Override
            public void onSuccess(RegistrCall result) {
                if (mView != null) {
                    mView.hideLoading();
                    mView.onQuickRegistration(result);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mView != null) {
                    mView.hideLoading();
                    ToastUtils.showToast(mContext, "添加患者失败");
                }
            }
        });
    }

    // 获取所有患者
    public void getPatient(String clientVersion) {
        mModel.allPatient(mContext, clientVersion, new ResponseListener<List<Patient>>() {
            @Override
            public List<Patient> convert(String jsonStr) {
                return JSON.parseArray(jsonStr, Patient.class);
            }

            @Override
            public void onSuccess(List<Patient> result) {
                if (mView != null) {
                    mView.onPatientResult(result);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode));
                }
            }
        });
    }

    /**
     * 获取医生列表
     */
    public void getDoctorInfo(String clinicId, int type) {
        if (type == 0) {
            mView.showLoading("加载中");
        }
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
                    ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode));
                    mView.hideLoading();
                }
            }
        });
    }
}
