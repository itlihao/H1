package com.zhiyihealth.registration.lib_base.entity;


import com.zhiyihealth.registration.lib_base.data.CacheDataSource;

import java.util.ArrayList;

public class DoctorInfoCheck extends DoctorInfo {

    public static ArrayList<DoctorInfoCheck> transformation(ArrayList<DoctorInfo> infos) {
        ArrayList<DoctorInfoCheck> result = new ArrayList<>();
        DoctorInfoCheck check = new DoctorInfoCheck();
        /*check.setRealName("全部");
        check.setSysUserId("");
        check.setCheck(false);
        result.add(check);*/
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
        /*for (DoctorInfo info : infos) {
            check = new DoctorInfoCheck();
            check.setRealName(info.getRealName());
            check.setSysUserId(info.getSysUserId());
            check.setCheck(false);
            result.add(check);
        }*/
        return result;
    }

    public static ArrayList<DoctorInfoCheck> transformationNoAll(ArrayList<DoctorInfo> infos){
        ArrayList<DoctorInfoCheck> result = new ArrayList<>();
        DoctorInfoCheck check ;

        if(infos==null){
            return result;
        }
        for (int i=0;i<infos.size();i++){
            check = new DoctorInfoCheck();
            check.setRealName(infos.get(i).getRealName());
            check.setSysUserId(infos.get(i).getSysUserId());
            check.setSex(infos.get(i).getSex());
            if(CacheDataSource.getDoctorMainId().equals(infos.get(i).getSysUserId())){
                check.setCheck(true);
//                CacheDataSource.setPtposition(i+1);
            }else{
                check.setCheck(false);
            }
            result.add(check);
        }
        return result;
    }

    public static ArrayList<DoctorInfoCheck> transformationNoCheck(ArrayList<DoctorInfo> infos){
        ArrayList<DoctorInfoCheck> result = new ArrayList<>();
        DoctorInfoCheck check ;

        if(infos==null){
            return result;
        }
        for (int i=0;i<infos.size();i++){
            check = new DoctorInfoCheck();
            check.setRealName(infos.get(i).getRealName());
            check.setSysUserId(infos.get(i).getSysUserId());
            check.setSex(infos.get(i).getSex());
            check.setCheck(false);
            result.add(check);
        }
        return result;
    }

    private boolean isCheck;
    private boolean isFulled = false;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isFulled() {
        return isFulled;
    }

    public void setFulled(boolean fulled) {
        isFulled = fulled;
    }
}
