package com.hospital.s1m.lib_base.constants;

/**
 * Created by wyl on 2017/6/10
 *
 * @author Lihao
 */
public interface Urls {
    // beta地址
//    String WORKBENCH = "https://bt-hm.zhiyimall.com";
//    String QRURL = "https://bt-clinicpe.yunzhenshi.com";
//    String PATIENT = "http://bt-patientcenter.yunzhenshi.com.cn";

    // 线上地址
     String WORKBENCH = "https://quick.yunzhenshi.com";
     String QRURL = "https://clinicpe.zhiyijiankang.com";
     String PATIENT = "http://patient.yunzhenshi.com/";


//   String usercenter="http://192.168.15.232";//登录前和登录
//   String WORKBENCH="http://192.168.14.33:8080";//登录前和登录
//   String PATIENT="http://192.168.15.237";

    // 登录
    String LOGIN = "/login/login";
    // 添加待就诊
    String SAVE_PATIENT_AND_REGISTRATION = "/registration/savePatientAndRegistration";
    // 获取所有患者
    String GET_ALL_PATIENT = "/clinicPatient/syClinicPatient";

    // 医生列表
    String GET_EMPLOY_LIST = "/clinicEmploy/getEmployList";
    // 快速挂号
    String QUICK_REGISTRATION = "/registration/firstRegisration";
    String QUICK_REGISTRATION_ID = "/registration/firstRegistrationBySysId";
}
