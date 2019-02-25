package com.hospital.s1m.lib_base.data;

import android.os.Bundle;

import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/5 0005.
 * 缓存数据，静态变量保存
 * 应保证缓存的数据只有在登录成功被存储，注销需主动清除缓存
 */

public class CacheDataSource {
    private static String clinicId;
    private static String doctorMainId;
    private static String imei;
    private static String type;
    private static String v;
    private static String baseUrl;
    private static String userToken;
    private static String userName;
    private static String clinicName;
    private static String userType;
    private static ArrayList<DoctorInfo> clinicDoctorInfo;
    private static String nowdoctorid;
    private static String popstatus;
    private static int ptposition;
    private static List<Patient> allPatient;

    public static int getPtposition() {
        return ptposition;
    }

    public static void setPtposition(int ptposition) {
        CacheDataSource.ptposition = ptposition;
    }

    public static String getPopstatus() {
        return popstatus;
    }

    public static void setPopstatus(String popstatus) {
        CacheDataSource.popstatus = popstatus;
    }

    public static String getNowdoctorid() {
        return nowdoctorid;
    }

    public static void setNowdoctorid(String nowdoctorid) {
        CacheDataSource.nowdoctorid = nowdoctorid;
    }

    public static String getUserToken() {
        return userToken;
    }

    public static void setUserToken(String userToken) {
        CacheDataSource.userToken = userToken;
    }

    public static String getClinicId() {
        return clinicId;
    }

    public static void setClinicId(String clinicId) {
        CacheDataSource.clinicId = clinicId;
    }

    public static String getDoctorMainId() {
        return doctorMainId;
    }

    public static void setDoctorMainId(String doctorMainId) {
        CacheDataSource.doctorMainId = doctorMainId;
    }

    public static String getImei() {
        return imei;
    }

    public static void setImei(String imei) {
        CacheDataSource.imei = imei;
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        CacheDataSource.type = type;
    }

    public static String getV() {
        return v;
    }

    public static void setV(String v) {
        CacheDataSource.v = v;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        CacheDataSource.userName = userName;
    }

    public static String getClinicName() {
        return clinicName;
    }

    public static void setClinicName(String clinicName) {
        CacheDataSource.clinicName = clinicName;
    }

    public static ArrayList<DoctorInfo> getClinicDoctorInfo() {
        return clinicDoctorInfo;
    }

    public static void setClinicDoctorInfo(ArrayList<DoctorInfo> clinicDoctorInfo) {
        CacheDataSource.clinicDoctorInfo = clinicDoctorInfo;
    }

    public static String getUserType() {
        return userType;
    }

    public static void setUserType(String userType) {
        CacheDataSource.userType = userType;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        CacheDataSource.baseUrl = baseUrl;
    }

    public static List<Patient> getAllPatient() {
        return allPatient;
    }

    public static void setAllPatient(List<Patient> allPatient) {
        CacheDataSource.allPatient = allPatient;
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        clinicId = "";
        doctorMainId = "";
        userName = "";
        clinicName = "";
        nowdoctorid = "";
        popstatus = "";
        clinicDoctorInfo = null;
    }

    public static void saveData(Bundle outState) {
        outState.putString("clinicId", clinicId);
        outState.putString("doctorMainId", doctorMainId);
        outState.putString("imei", imei);
        outState.putString("type", type);
        outState.putString("v", v);
        outState.putString("popstatus", popstatus);
        outState.putString("nowdoctor", nowdoctorid);
        outState.putString("userToken", userToken);
        outState.putString("baseUrl", baseUrl);
        outState.putString("userName", userName);
        outState.putString("clinicName", clinicName);
        outState.putSerializable("clinicDoctorInfo", clinicDoctorInfo);
        outState.putInt("ptposition", ptposition);
    }

    public static void restoreData(Bundle savedInstanceState) {
        clinicId = savedInstanceState.getString("clinicId");
        doctorMainId = savedInstanceState.getString("doctorMainId");
        imei = savedInstanceState.getString("imei");
        type = savedInstanceState.getString("type");
        doctorMainId = savedInstanceState.getString("nowdoctor");
        v = savedInstanceState.getString("v");
        userToken = savedInstanceState.getString("userToken");
        popstatus = savedInstanceState.getString("popstatus");
        baseUrl = savedInstanceState.getString("baseUrl");
        userName = savedInstanceState.getString("userName");
        ptposition = savedInstanceState.getInt("ptposition");
        clinicName = savedInstanceState.getString("clinicName");
        clinicDoctorInfo = (ArrayList<DoctorInfo>) savedInstanceState.getSerializable("clinicDoctorInfo");
    }
}
