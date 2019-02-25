package com.hospital.s1m.lib_user.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.hospital.s1m.lib_base.base.BaseActivity;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.contract.MainContract;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_base.entity.LoginContent;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_user.R;
import com.hospital.s1m.lib_user.bean.LoginParmar;
import com.hospital.s1m.lib_user.model.LoginModel;
import com.hospital.s1m.lib_user.presenter.LoginPresenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lihao
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, MainContract.LoginView {
    private static final String CLINIC = "clinic";
    private EditText mUserTel;
    private EditText mUserPwd;
    private AppCompatCheckBox mRememberPwd;

    private TextView mClinic;

    private LoginPresenter mPresenter;

    private CC cc = null;

    private MHandler mhandler = new MHandler();

    @SuppressLint("HandlerLeak")
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mRememberPwd.isChecked()) {
                        doLogin();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        mPresenter = new LoginPresenter(LoginActivity.this, new LoginModel(), this);
        initData();
        mhandler.sendEmptyMessageDelayed(1, 1500);
    }

    private void initView() {
        mUserTel = findViewById(R.id.et_login_username);
        mUserPwd = findViewById(R.id.et_login_password);
        mRememberPwd = findViewById(R.id.remember_pwd);
        mClinic = findViewById(R.id.tv_hospital_name);
        TextView mForgetPwd = findViewById(R.id.forget_pwd);
        Button mBtnLogin = findViewById(R.id.btn_login);

        mForgetPwd.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        LoginParmar parmar = mPresenter.getInitData();
        mUserTel.setText(parmar.getLoginName());
        mUserPwd.setText(parmar.getPassword());

        mRememberPwd.setChecked(parmar.isForget());
//        mLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

        String clinic = (String) SPDataSource.get(this, CLINIC, "");
        if (clinic == null) {
            return;
        }
        if (clinic.length() > 12) {
            mClinic.setText(clinic.substring(0, 12) + "...");
        } else {
            mClinic.setText(clinic);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.forget_pwd) {
            Intent intent = new Intent(LoginActivity.this, FindActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_login) {
            doLogin();
        }
    }

    private void doLogin() {
        String phone = mUserTel.getText().toString().trim();
        String pwd = mUserPwd.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast(this, R.string.toast_input_phone);
        } else if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast(this, R.string.toast_input_pwd);
        } else if (!isPhoneNum(phone)) {
            ToastUtils.showToast(this, R.string.toast_input_phone_err);
        } else {
            LoginParmar parmar = new LoginParmar(this, phone, pwd, mRememberPwd.isChecked());
            mPresenter.doLogin(parmar);
        }
    }

    private boolean isPhoneNum(String str) {
        boolean isPhone = false;
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(str);
        if (!TextUtils.isEmpty(str) && matcher.matches()) {
            isPhone = true;
        }
        return isPhone;
    }

    @Override
    public void onLoginResult(LoginContent result) {
        boolean quickModel = (boolean) SPDataSource.get(this, "quickRegistration", true);
        if (quickModel) {
            cc = CC.obtainBuilder(Components.COMPONENT_APP_MAIN)
                    .setActionName(Components.COMPONENT_APP_JUMP)
                    .addParam("token", result.getUserToken())
                    .build();
        } else {
            cc = CC.obtainBuilder(Components.COMPONENT_APP_MAIN)
                    .setActionName(Components.COMPONENT_APP_JUMPN)
                    .addParam("token", result.getUserToken())
                    .build();
        }

        cc.call();
        finish();
    }

    @Override
    public void onResetResult(String result) {

    }
}
