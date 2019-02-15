package com.zhiyihealth.registration.lib_base.model;

import android.content.Context;
import android.text.TextUtils;

import com.zhiyihealth.registration.lib_base.constants.Urls;
import com.zhiyihealth.registration.lib_base.data.CacheDataSource;
import com.zhiyihealth.registration.lib_base.data.NetDataSource;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfo;
import com.zhiyihealth.registration.lib_base.entity.QuickRegistr;
import com.zhiyihealth.registration.lib_base.listener.ResponseListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Lihao
 * @date 2019-2-14
 * Email heaolihao@163.com
 */
public class RegistrationModel {

    public void quickRegistration(Context context, String clinicId, String sysUserId, ResponseListener<QuickRegistr> listener) {
        CacheDataSource.setBaseUrl(Urls.workbench);
        HashMap<String, String> clinic = new HashMap<>(2);
        clinic.put("clinicId", clinicId);
        if (!TextUtils.isEmpty(sysUserId)) {
            clinic.put("sysUserId", sysUserId);
        }
        NetDataSource.postDetail(context, Urls.QUICK_REGISTRATION, clinic, listener);
    }

    public void getEmployList(Context context, String clinicId, ResponseListener<ArrayList<DoctorInfo>> listener) {
        CacheDataSource.setBaseUrl(Urls.workbench);
        HashMap<String, String> clinic = new HashMap<>(1);
        clinic.put("clinicId", clinicId);
        NetDataSource.postNoHeader(context, Urls.GET_EMPLOY_LIST, clinic, listener);
    }

    public void saveSystemData(ArrayList<DoctorInfo> result) {
        CacheDataSource.setClinicDoctorInfo(result);
    }
}
