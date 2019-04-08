package com.hospital.s1m.lib_user.model;

import android.content.Context;

import com.hospital.s1m.lib_base.constants.Urls;
import com.hospital.s1m.lib_base.data.CacheDataSource;
import com.hospital.s1m.lib_base.data.NetDataSource;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.LoginContent;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.hospital.s1m.lib_user.bean.LoginParmar;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public class LoginModel {
    private static final String USERNAME = "userName";
    private static final String PASSWORD = "password";
    private static final String FORGET = "forget";
    private static final String CLINIC = "clinic";

    /**
     * 登录
     */
    public void login(Context context, LoginParmar parmar, ResponseListener<LoginContent> listener) {
        CacheDataSource.setBaseUrl(Urls.workbench);
        NetDataSource.post(context, Urls.login, parmar, listener);
    }

    /**
     * 保存诊所信息
     */
    public void saveSystemData(Context context, LoginContent loginContent) {
        CacheDataSource.setClinicId(loginContent.getClinicId());
        CacheDataSource.setDoctorMainId(loginContent.getUserId());
        CacheDataSource.setUserName(loginContent.getUserName());
        CacheDataSource.setClinicName(loginContent.getClinicName());
        SPDataSource.put(context, CLINIC, loginContent.getClinicName());
        CacheDataSource.setUserType(loginContent.getUserType());
        CacheDataSource.setUserToken(loginContent.getUserToken());
    }

    /**
     * 保存用户名和密码
     */
    public void saveUserNameAndPassword(Context context, LoginParmar parmar) {
        try {
            if (parmar.isForget()) {
                SPDataSource.put(context, USERNAME, parmar.getLoginName());
                SPDataSource.put(context, PASSWORD, parmar.getPassword());
            } else {
                SPDataSource.put(context, USERNAME, parmar.getLoginName());
                SPDataSource.put(context, PASSWORD, "");
            }
            SPDataSource.put(context, FORGET, parmar.isForget());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LoginParmar getInitData(Context mContext) {
        String username = (String) SPDataSource.get(mContext, USERNAME, "");
        String password = (String) SPDataSource.get(mContext, PASSWORD, "");
        boolean forget = (boolean) SPDataSource.get(mContext, FORGET, false);
        return new LoginParmar(username, password, forget);
    }
}
