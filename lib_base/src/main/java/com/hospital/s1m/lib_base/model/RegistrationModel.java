package com.hospital.s1m.lib_base.model;

import android.content.Context;
import android.text.TextUtils;

import com.hospital.s1m.lib_base.constants.Urls;
import com.hospital.s1m.lib_base.data.CacheDataSource;
import com.hospital.s1m.lib_base.data.NetDataSource;
import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.Patient;
import com.hospital.s1m.lib_base.entity.PatientAndRegistrationParmar;
import com.hospital.s1m.lib_base.entity.RegistrCall;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.hospital.s1m.lib_base.utils.DeviceInfoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lihao
 * @date 2019-2-14
 * Email heaolihao@163.com
 */
public class RegistrationModel {

    /**
     * 快速挂号
     */
    public void quickRegistration(Context context, String clinicId, String sysUserId, String next, ResponseListener<RegistrCall> listener) {
        CacheDataSource.setBaseUrl(Urls.WORKBENCH);
        HashMap<String, String> clinic = new HashMap<>(4);
        clinic.put("clinicId", clinicId);
        clinic.put("deviceNo", DeviceInfoUtils.getDeviceSN());
        if (next != null) {
            clinic.put("periodType", next);
        }
        if (!TextUtils.isEmpty(sysUserId)) {
            clinic.put("sysUserId", sysUserId);
            NetDataSource.postDetail(context, Urls.QUICK_REGISTRATION_ID, clinic, listener);
        } else {
            NetDataSource.postDetail(context, Urls.QUICK_REGISTRATION, clinic, listener);
        }

    }

    /**
     * 医生列表
     */
    public void getEmployList(Context context, String clinicId, ResponseListener<ArrayList<DoctorInfo>> listener) {
        CacheDataSource.setBaseUrl(Urls.WORKBENCH);
        HashMap<String, String> clinic = new HashMap<>(1);
        clinic.put("clinicId", clinicId);
        NetDataSource.postNoHeader(context, Urls.GET_EMPLOY_LIST, clinic, listener);
    }

    /**
     * 添加患者并挂号
     */
    public void saveAndRegistration(Context context, PatientAndRegistrationParmar info, ResponseListener<RegistrCall> listener) {
        CacheDataSource.setBaseUrl(Urls.WORKBENCH);
        NetDataSource.post(context, Urls.SAVE_PATIENT_AND_REGISTRATION, info, listener);
    }

    /**
     * 查询患者
     */
    public void allPatient(Context context, String clientVersion, ResponseListener<List<Patient>> listener) {
        CacheDataSource.setBaseUrl(Urls.PATIENT);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("clinicId", CacheDataSource.getClinicId());
        hashMap.put("clientVersion", clientVersion);
        NetDataSource.post(context, Urls.GET_ALL_PATIENT, hashMap, listener);
    }

    public void saveSystemData(ArrayList<DoctorInfo> result) {
        CacheDataSource.setClinicDoctorInfo(result);
    }
}
