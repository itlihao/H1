package com.zhiyihealth.registration.lib_user.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfo;
import com.zhiyihealth.registration.lib_base.entity.LoginContent;
import com.zhiyihealth.registration.lib_base.listener.ResponseListener;
import com.zhiyihealth.registration.lib_base.utils.MD5Utils;
import com.zhiyihealth.registration.lib_user.bean.LoginParmar;
import com.zhiyihealth.registration.lib_user.contract.MainContract;
import com.zhiyihealth.registration.lib_user.model.LoginModel;

import java.util.ArrayList;

/**
 *
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public class LoginPresenter {
    private MainContract.LoginView mLoginView;

    private LoginModel mLoginModel;

    private Context mContext;

    public LoginPresenter(Context context, LoginModel loginModel, MainContract.LoginView loginView) {
        this.mContext = context;
        this.mLoginModel = loginModel;
        this.mLoginView = loginView;
    }

    public void doLogin(LoginParmar loginParmar) {
        mLoginView.showLoading("加载中");
        if (loginParmar.getPassword().length() != 32) {
            loginParmar.setPassword(MD5Utils.md5(loginParmar.getPassword()));
        }
        mLoginModel.login(mContext, loginParmar, new ResponseListener<LoginContent>() {
            @Override
            public LoginContent convert(String jsonStr) {
                return JSON.parseObject(jsonStr, LoginContent.class);
            }

            @Override
            public void onSuccess(LoginContent result) {
                if (mLoginView != null) {
                    mLoginModel.saveSystemData(mContext, result, null);
                    getDoctorInfo(loginParmar, result);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mLoginView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
//                    ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode));e10adc3949ba59abbe56e057f20f883e
                    mLoginView.hideLoading();
                }
            }
        });
    }

    private void getDoctorInfo(LoginParmar loginParmar, LoginContent resultLogin) {
        mLoginModel.getDoctorsInfo(mContext, resultLogin.getClinicId(), new ResponseListener<ArrayList<DoctorInfo>>() {
            @Override
            public ArrayList<DoctorInfo> convert(String jsonStr) {
                return JSON.parseObject(jsonStr, new TypeReference<ArrayList<DoctorInfo>>() {
                });
            }

            @Override
            public void onSuccess(ArrayList<DoctorInfo> result) {
                if (mLoginView != null) {
                    mLoginView.hideLoading();
                    successOperation(loginParmar, resultLogin, result);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mLoginView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
//                    ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode));
                    mLoginView.hideLoading();
                }
            }
        });
    }

    private void successOperation(LoginParmar loginParmar, LoginContent loginContent, ArrayList<DoctorInfo> result) {
        //1.是否保存密码
        mLoginModel.saveUserNameAndPassword(mContext, loginParmar);
        //2.保存系统信息
        mLoginModel.saveSystemData(mContext, loginContent, result);
        //3.做界面业务
        mLoginView.onLoginResult(loginContent);
    }

    public LoginParmar getInitData() {
        return mLoginModel.getInitData(mContext);
    }
}
