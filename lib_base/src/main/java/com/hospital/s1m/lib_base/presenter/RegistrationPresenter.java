package com.hospital.s1m.lib_base.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.billy.cc.core.component.CC;
import com.hospital.s1m.lib_base.BaseApplication;
import com.hospital.s1m.lib_base.R;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.contract.MainContract;
import com.hospital.s1m.lib_base.data.CacheDataSource;
import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.Patient;
import com.hospital.s1m.lib_base.entity.PatientAndRegistrationParmar;
import com.hospital.s1m.lib_base.entity.RegistrCall;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.hospital.s1m.lib_base.model.RegistrationModel;
import com.hospital.s1m.lib_base.utils.CodeUtils;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_base.view.MineDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

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
                mView.hideLoading();
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
            public void onFailed(String errorCode, String errorInfo, String type) {
                if (mView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    if ("2020106".equals(errorCode) || "2020220".equals(errorCode)) {

                        mView.onPeriodFullResult(errorInfo, type);
                    } else {
                        String info = CodeUtils.setCode(errorCode, errorInfo);
                        if (!TextUtils.isEmpty(info)) {
                            ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode, errorInfo));
                        }
                    }
                    mView.hideLoading();
                    mView.onFailed();
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
            public void onFailed(String errorCode, String errorInfo, String type) {
                if (mView != null) {
                    mView.hideLoading();

                    if ("2010109".equals(errorCode)) {
                        Observable.empty().observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> {
                                    //添加"Yes"按钮
                                    //添加取消
                                    Activity curActivity = BaseApplication.getCurActivity();
                                    AlertDialog alertDialog2 = new MineDialog.Builder(curActivity)
                                            .setMessage(R.string.basic_return)
                                            .setPositiveButton(R.string.basic_comfirm, (dialogInterface, i) -> {
                                                CacheDataSource.clearCache();
                                                CC.obtainBuilder(Components.COMPONENT_USER)
                                                        .setActionName(Components.COMPONENT_USER_JUMP)
                                                        .build().call();
                                                curActivity.finish();
                                            }).create();
                                    alertDialog2.show();
                                    alertDialog2.setOnKeyListener((dialog, keyCode, event) -> {
                                        if (KeyEvent.KEYCODE_BACK == keyCode) {
                                            // TODO 禁用返回键
                                            System.out.print("");
                                            return true;
                                        }
                                        return false;
                                    });
                                    alertDialog2.setCancelable(false);
                                }).subscribe();
                    } else {
                        String info = CodeUtils.setCode(errorCode, errorInfo);
                        if (!TextUtils.isEmpty(info)) {
                            ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode, errorInfo));
                        }
                    }
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
            public void onFailed(String errorCode, String errorInfo, String type) {
                if (mView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    String info = CodeUtils.setCode(errorCode, errorInfo);
                    if (!TextUtils.isEmpty(info)) {
                        ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode, errorInfo));
                    }
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
            public void onFailed(String errorCode, String errorInfo, String type) {
                if (mView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    String info = CodeUtils.setCode(errorCode, errorInfo);
                    if (!TextUtils.isEmpty(info)) {
                        ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode, errorInfo));
                    }
                    mView.hideLoading();
                }
            }
        });
    }
}
