package com.hospital.s1m.lib_base.contract;

import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.LoginContent;
import com.hospital.s1m.lib_base.base.BaseView;
import com.hospital.s1m.lib_base.entity.Patient;
import com.hospital.s1m.lib_base.entity.RegistrCall;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public interface MainContract {

    interface LoginView extends BaseView {
        /**
         * 登录返回结果
         */
        void onLoginResult(LoginContent result);
        /**
         * 修改密码结果
         */
        void onResetResult(String result);
    }

    interface RegistrationView extends BaseView {
        /**
         *快速挂号返回结果
         */
        void onQuickRegistration(RegistrCall result);
        /**
         * 医生列表返回
         */
        void onDoctorResult(ArrayList<DoctorInfo> result);
        /**
         * 患者查询结果
         */
        void onPatientResult(List<Patient> result);
        /**
         * 医生该时段内已约满
         */
        void onPeriodFullResult(String result, String type);

        void onFailed();
    }
}
