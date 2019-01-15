package com.zhiyihealth.registration.lib_user.contract;

import com.zhiyihealth.registration.lib_base.entity.LoginContent;
import com.zhiyihealth.registration.lib_base.base.BaseView;

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
}
