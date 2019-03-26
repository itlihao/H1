package com.hospital.s1m.lib_base.entity;

import java.io.Serializable;

public class DoctorInfo implements Serializable {
    private String realName;
    private String doctorId;
    private String sex;
    private String firstRegi;
    private int waitNum;


    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFirstRegi() {
        return firstRegi;
    }

    public void setFirstRegi(String firstRegi) {
        this.firstRegi = firstRegi;
    }

    public int getWaitNum() {
        return waitNum;
    }

    public void setWaitNum(int waitNum) {
        this.waitNum = waitNum;
    }

}
