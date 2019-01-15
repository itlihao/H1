package com.zhiyihealth.registration.lib_user.model;

import android.content.Context;

import com.zhiyihealth.registration.lib_base.constants.Urls;
import com.zhiyihealth.registration.lib_base.data.CacheDataSource;
import com.zhiyihealth.registration.lib_base.data.NetDataSource;
import com.zhiyihealth.registration.lib_base.data.SPDataSource;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfo;
import com.zhiyihealth.registration.lib_base.entity.LoginContent;
import com.zhiyihealth.registration.lib_base.listener.ResponseListener;
import com.zhiyihealth.registration.lib_user.bean.LoginParmar;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lihao on 2019-1-9.
 * Email heaolihao@163.com
 */
public class LoginModel {
    private static final String USERNAME = "userName";
    private static final String PASSWORD = "password";
    private static final String FORGET = "forget";
    private static final String CLINIC = "clinic";

    public void login(Context context, LoginParmar parmar, ResponseListener<LoginContent> listener) {
        CacheDataSource.setBaseUrl(Urls.usercenter);
        NetDataSource.post(context, Urls.login, parmar, listener);
    }

    public void saveSystemData(Context context, LoginContent loginContent, ArrayList<DoctorInfo> result) {
        CacheDataSource.setClinicId(loginContent.getClinicId());
        CacheDataSource.setDoctorMainId(loginContent.getUserId());
        CacheDataSource.setUserName(loginContent.getUserName());
        CacheDataSource.setClinicName(loginContent.getClinicName());
        SPDataSource.put(context, CLINIC, loginContent.getClinicName());
        CacheDataSource.setUserType(loginContent.getUserType());
        CacheDataSource.setClinicDoctorInfo(result);
        CacheDataSource.setUserToken(loginContent.getUserToken());
    }

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

    public void getDoctorsInfo(Context context, String clinciId, ResponseListener<ArrayList<DoctorInfo>> listener) {
        HashMap<String, String> clinci = new HashMap<>();
        clinci.put("clinicId", clinciId);
        NetDataSource.post(context, Urls.getListByClinicId, clinci, listener);
    }

    public LoginParmar getInitData(Context mContext) {
        String username = (String) SPDataSource.get(mContext, USERNAME, "");
        String password = (String) SPDataSource.get(mContext, PASSWORD, "");
        boolean forget = (boolean) SPDataSource.get(mContext, FORGET, false);
        return new LoginParmar(username, password, forget);
    }
}
