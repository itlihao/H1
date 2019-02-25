package com.hospital.s1m.lib_user.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.hospital.s1m.lib_base.contract.MainContract;
import com.hospital.s1m.lib_base.entity.LoginContent;
import com.hospital.s1m.lib_base.jpush.TagAliasOperatorHelper;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.hospital.s1m.lib_base.utils.CodeUtils;
import com.hospital.s1m.lib_base.utils.MD5Utils;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_user.bean.LoginParmar;
import com.hospital.s1m.lib_user.model.LoginModel;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.hospital.s1m.lib_base.jpush.TagAliasOperatorHelper.ACTION_SET;
import static com.hospital.s1m.lib_base.jpush.TagAliasOperatorHelper.sequence;

/**
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public class LoginPresenter {
    private MainContract.LoginView mLoginView;

    private LoginModel mLoginModel;

    private Context mContext;

    private String pwd;

    public LoginPresenter(Context context, LoginModel loginModel, MainContract.LoginView loginView) {
        this.mContext = context;
        this.mLoginModel = loginModel;
        this.mLoginView = loginView;
    }

    public void doLogin(LoginParmar loginParmar) {
        mLoginView.showLoading("加载中");
        if (loginParmar.getPassword().length() != 32) {
            pwd = loginParmar.getPassword();
            loginParmar.setPassword(MD5Utils.md5(loginParmar.getPassword()));
        }
        mLoginModel.login(mContext, loginParmar, new ResponseListener<LoginContent>() {
            @Override
            public LoginContent convert(String jsonStr) {
                return JSON.parseObject(jsonStr, LoginContent.class);
            }

            @Override
            public void onSuccess(LoginContent result) {
                if (mLoginView != null) {
                    successOperation(loginParmar, result);
                    mLoginView.hideLoading();
                    setAliasAndTags(result.getUserToken());
                }
            }

            @Override
            public void onFailed(String errorCode, String errorInfo) {
                if (mLoginView != null) {
                    System.out.println(errorCode + "信息" + errorInfo);
                    ToastUtils.showToast(mContext, "" + CodeUtils.setCode(errorCode));
                    mLoginView.hideLoading();
                }
            }
        });
    }

    private void successOperation(LoginParmar loginParmar, LoginContent loginContent) {
        //1.是否保存密码
        loginParmar.setPassword(pwd);
        mLoginModel.saveUserNameAndPassword(mContext, loginParmar);
        //2.保存系统信息
        mLoginModel.saveSystemData(mContext, loginContent);
        //3.做界面业务
        mLoginView.onLoginResult(loginContent);
    }

    private void setAliasAndTags(String sn) {
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = ACTION_SET;
        sequence++;
        tagAliasBean.alias = sn;
        // 设置alias
        Set<String> tagSet = new LinkedHashSet<>();
        tagSet.add(sn);
        tagAliasBean.tags = tagSet;
        tagAliasBean.isAliasAction = true;
        TagAliasOperatorHelper.getInstance().handleAction(mContext, sequence, tagAliasBean);
        // 设置tags
        tagAliasBean.isAliasAction = false;
        TagAliasOperatorHelper.getInstance().handleAction(mContext, sequence, tagAliasBean);
    }

    public LoginParmar getInitData() {
        return mLoginModel.getInitData(mContext);
    }
}
