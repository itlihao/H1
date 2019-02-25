package com.hospital.s1m.lib_base.entity;

/**
 * Created by android on 2017/8/8.
 */

public class Header {
    private String clinicId;
    private String v;
    private String imei;
    private String type;
    private String doctorMainId;
    private long currentTime;
    private String userToken;

    public String getClinicId() {
        return clinicId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoctorMainId() {
        return doctorMainId;
    }

    public void setDoctorMainId(String doctorMainId) {
        this.doctorMainId = doctorMainId;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
