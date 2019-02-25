package com.hospital.s1m.lib_base.entity;

import java.io.Serializable;

public class PatientAndRegistrationParmar implements Serializable {
    /**
     * id : e0c715ae38a14ef49b8f8f54e65b7d2b
     * userName : 天台实施
     * userShortName : TTSS
     * birthday : 2018-07-19
     * sex : 1
     * phone :
     * homeAddress :
     * sysUserIdRegi : ff808081628ba9d10162a422768f4941
     * idCardNo :
     */

    private String id;
    private String userName;
    private String userShortName;
    private String birthday;
    private String year;
    private String month;
    private String day;
    private int sex;
    private String phone;
    private String homeAddress;
    private String sysUserIdRegi;
    private String idCardNo;
    private String doctorName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserShortName() {
        return userShortName;
    }

    public void setUserShortName(String userShortName) {
        this.userShortName = userShortName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getSysUserIdRegi() {
        return sysUserIdRegi;
    }

    public void setSysUserIdRegi(String sysUserIdRegi) {
        this.sysUserIdRegi = sysUserIdRegi;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
