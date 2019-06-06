package com.hospital.s1m.lib_user.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.hospital.s1m.lib_base.base.BaseActivity;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.contract.MainContract;
import com.hospital.s1m.lib_base.entity.LoginContent;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_user.R;
import com.hospital.s1m.lib_user.bean.LoginParmar;
import com.hospital.s1m.lib_user.model.LoginModel;
import com.hospital.s1m.lib_user.presenter.LoginPresenter;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lihao
 */
public class LoginMActivity extends BaseActivity implements View.OnClickListener, MainContract.LoginView {
    private static final String CLINIC = "clinic";
    private EditText mUserTel;
    private EditText mUserPwd;
    private AppCompatCheckBox mRememberPwd;

    private LoginPresenter mPresenter;

    private CC cc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        mPresenter = new LoginPresenter(LoginMActivity.this, new LoginModel(), this);
        initData();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        mUserTel = findViewById(R.id.et_login_username);
        mUserPwd = findViewById(R.id.et_login_password);
        mRememberPwd = findViewById(R.id.remember_pwd);
        TextView mForgetPwd = findViewById(R.id.forget_pwd);
        Button mBtnLogin = findViewById(R.id.btn_login);

        mBtnLogin.setOnClickListener(this);
        RxView.clicks(mBtnLogin)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> doLogin());
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        LoginParmar parmar = mPresenter.getInitData();
        mUserTel.setText(parmar.getLoginName());
        mUserPwd.setText(parmar.getPassword());
        mRememberPwd.setChecked(parmar.isForget());
//        mLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_login_m;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.forget_pwd) {
            Intent intent = new Intent(LoginMActivity.this, FindActivity.class);
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
        cc = CC.obtainBuilder(Components.COMPONENT_APP_MAIN)
                .setActionName(Components.COMPONENT_APP_JUMP)
                .addParam("token", result.getUserToken())
                .build();
        cc.call();
        finish();
    }

    @Override
    public void onResetResult(String result) {

    }
}
