package com.hospital.s1m.lib_base.entity;

import java.util.List;

/**
 * Created by Lihao on 2019-2-14.
 * Email heaolihao@163.com
 */
public class RegistrCall {

    /**
     * doctorName : 耿小敏
     * periodType : 1
     * registrationNo : 7
     * registrationId : 5dd69b9433ec11e9833c00163e08c3d6
     * sysUserId : 2c92e0f36817268f0168177f0584000c
     * timeTable : [{"periodType":1,"startTime":"08:00","endTime":"12:00"},{"periodType":2,"startTime":"13:00","endTime":"18:00"},{"periodType":3,"startTime":"19:00","endTime":"21:00"}]
     */

    private String doctorName;
    /**
     * 1/上午-2/中午-3/晚上
     */
    private int periodType;
    private int registrationNo;
    private String registrationId;
    private String sysUserId;
    private List<TimeTableBean> timeTable;
    private String waitNum;

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getPeriodType() {
        return periodType;
    }

    public void setPeriodType(int periodType) {
        this.periodType = periodType;
    }

    public int getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(int registrationNo) {
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

    public List<TimeTableBean> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(List<TimeTableBean> timeTable) {
        this.timeTable = timeTable;
    }

    public String getWaitNum() {
        return waitNum;
    }

    public void setWaitNum(String waitNum) {
        this.waitNum = waitNum;
    }

    public static class TimeTableBean {
        /**
         * periodType : 1
         * startTime : 08:00
         * endTime : 12:00
         */

        private int periodType;
        private String startTime;
        private String endTime;

        public int getPeriodType() {
            return periodType;
        }

        public void setPeriodType(int periodType) {
            this.periodType = periodType;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
