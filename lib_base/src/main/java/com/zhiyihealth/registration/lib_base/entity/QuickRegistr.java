package com.zhiyihealth.registration.lib_base.entity;

/**
 * Created by Lihao on 2019-2-14.
 * Email heaolihao@163.com
 */
public class QuickRegistr {
    private String registrationNo;
    private String registrationId;
    private String sysUserId;
    /**
     * 1/上午-2/中午-3/晚上
     */
    private int periodType;

    private String doctorName;

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(String sysUserId) {
        this.sysUserId = sysUserId;
    }

    public int getPeriodType() {
        return periodType;
    }

    public void setPeriodType(int periodType) {
        this.periodType = periodType;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
