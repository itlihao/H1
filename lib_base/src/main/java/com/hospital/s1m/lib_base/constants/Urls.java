package com.hospital.s1m.lib_base.constants;

/**
 * Created by wyl on 2017/6/10
 */
public interface Urls {
    String usercenter = "http://bt-usercenter.yunzhenshi.com.cn";//登录前和登录
    //    String workbench = "http://bt-workbench.yunzhenshi.com.cn";//登录前和登录
    String patient = "http://bt-patientcenter.yunzhenshi.com.cn";//患者

//   String usercenter="http://192.168.15.232";//登录前和登录
//   String workbench="http://192.168.15.230";//登录前和登录

//    String usercenter = "http://usersystem.yunzhenshi.com";//登录前和登录
//    String workbench = "http://workbench.yunzhenshi.com";//登录前和登录
//    String patient="http://patient.yunzhenshi.com";//患者

    String beforlogin = "/login/beforeLogin";//登录前
    String login = "/login/login";//登录
    String findList = "/precontract/findList";//已预约患者查询
    String joinRegistration = "/precontract/joinRegistration";//将患者加入待就诊
    String findRegistrationByPage = "/registration/findRegistrationByPage";//分页查询挂号列表
    String savePatientAndRegistration = "/registration/savePatientAndRegistration";//添加待就诊
    String changeStatus = "/registration/changeStatus";//叫号
    String getListByClinicId = "/clinicEmploy/getListByClinicId";//获取医生Id
    String getRegistrationCount = "/registration/getRegistrationCount";//获取登录医生的当天叫号统计
    String createCancelPrescription = "/prescription/createCancelPrescription";//接诊接口
    String getQrcodeOfClinic = "/qrcode/getQrcodeOfClinic";//接诊接口
    String findClinicNotice = "/clinic/findClinicNotice";//获取公告
    String saveClinicNotice = "/clinic/saveClinicNotice";//设置公告
    String getClinicSetting = "/arEmployTimetable/getClinicSetting";//排班-获取排班设置信息
    String updateClinicSetting = "/arEmployTimetable/updateClinicSetting";//排班-保存排班设置信息

    String CANCEL_REGISTRATION = "/registration/changeStatus";
    String GET_RESET_PWD_CODE = "/login/sendResetPasswordMsg";
    String RESET_PASSWORD = "/login/resetPassword";
    String GET_ALL_PATIENT = "/clinicPatient/syClinicPatient";

    String getCloseList = "/arBreakTime/get";//获取停诊设置
    String delCloseList = "/arBreakTime/del";//获取停诊设置
    String addCloseList = "/arBreakTime/update";//获取停诊设置


    String workbench = "http://120.79.241.190:8080";
    // 医生列表
    String GET_EMPLOY_LIST = "/clinicEmploy/getEmployList";
    // 快速挂号
    String QUICK_REGISTRATION = "/registration/firstRegisration";
}
