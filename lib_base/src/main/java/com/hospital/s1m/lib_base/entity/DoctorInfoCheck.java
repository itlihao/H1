package com.hospital.s1m.lib_base.entity;


import java.util.ArrayList;

public class DoctorInfoCheck extends DoctorInfo {

    /*public static ArrayList<DoctorInfoCheck> transformation(ArrayList<DoctorInfo> infos) {
        ArrayList<DoctorInfoCheck> result = new ArrayList<>();
        DoctorInfoCheck check = new DoctorInfoCheck();
        *//*check.setRealName("全部");
        check.setSysUserId("");
        check.setCheck(false);
        result.add(check);*//*
        if (infos == null) {
            return result;
        }
        for (int i = 0; i < infos.size(); i++) {
            check = new DoctorInfoCheck();
            check.setRealName(infos.get(i).getRealName());
            check.setSysUserId(infos.get(i).getSysUserId());
            if (CacheDataSource.getDoctorMainId().equals(infos.get(i).getSysUserId())) {
                check.setCheck(true);
                CacheDataSource.setPtposition(i);
            } else {
                check.setCheck(false);
            }
            result.add(check);
        }
        *//*for (DoctorInfo info : infos) {
            check = new DoctorInfoCheck();
            check.setRealName(info.getRealName());
            check.setSysUserId(info.getSysUserId());
            check.setCheck(false);
            result.add(check);
        }*//*
        return result;
    }*/

    /*public static ArrayList<DoctorInfoCheck> transformationNoAll(ArrayList<DoctorInfo> infos) {
        ArrayList<DoctorInfoCheck> result = new ArrayList<>();
        DoctorInfoCheck check;

        if (infos == null) {
            return result;
        }
        for (int i = 0; i < infos.size(); i++) {
            check = new DoctorInfoCheck();
            check.setRealName(infos.get(i).getRealName());
            check.setSysUserId(infos.get(i).getSysUserId());
            check.setSex(infos.get(i).getSex());
            if (CacheDataSource.getDoctorMainId().equals(infos.get(i).getSysUserId())) {
                check.setCheck(true);
//                CacheDataSource.setPtposition(i+1);
            } else {
                check.setCheck(false);
            }
            result.add(check);
        }
        return result;
    }*/

    public static ArrayList<DoctorInfoCheck> transformationNoCheck(ArrayList<DoctorInfo> infos) {
        ArrayList<DoctorInfoCheck> result = new ArrayList<>();
        DoctorInfoCheck check;

        if (infos == null) {
            return result;
        }
        for (int i = 0; i < infos.size(); i++) {
            if (!("0").equals(infos.get(i).getFirstRegi())) {
                check = new DoctorInfoCheck();
                check.setRealName(infos.get(i).getRealName());
                check.setDoctorId(infos.get(i).getDoctorId());
                check.setSex(infos.get(i).getSex());
                check.setWaitNum(infos.get(i).getWaitNum());
                check.setFirstRegi(infos.get(i).getFirstRegi());
                check.setCheck(false);
                result.add(check);
            }
        }
        return result;
    }

    public static ArrayList<DoctorInfoCheck> transformation(ArrayList<DoctorInfo> infos) {
        ArrayList<DoctorInfoCheck> result = new ArrayList<>();
        DoctorInfoCheck check;

        if (infos == null) {
            return result;
        }
        for (int i = 0; i < infos.size(); i++) {
            check = new DoctorInfoCheck();
            check.setRealName(infos.get(i).getRealName());
            check.setDoctorId(infos.get(i).getDoctorId());
            check.setSex(infos.get(i).getSex());
            check.setWaitNum(infos.get(i).getWaitNum());
            check.setFirstRegi(infos.get(i).getFirstRegi());
            check.setCheck(false);
            result.add(check);
        }
        return result;
    }

    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isFulled() {
        return "2".equals(getFirstRegi());
    }

    public boolean isFirstRegi() {
        return "1".equals(getFirstRegi());
    }

    public boolean isUnFirstRegi() {
        return "0".equals(getFirstRegi());
    }
}
