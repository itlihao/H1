package com.zhiyihealth.registration.lib_base.base;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zhiyihealth.registration.lib_base.view.MDDialog;

import java.util.Objects;

/**
 *
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    private MDDialog mDialog;

    private MDDialog.Builder builder;

    private int type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (setContentView() != 0) {
            setContentView(setContentView());
        }


        builder = new MDDialog.Builder(this);
        builder.setShowTitle(false)
                .setShowMessage(true)
                .setShowAvi(true)
                .setWidthMaxDp(440)
                .setShowNegativeButton(false)
                .setShowPositiveButton(false)
                .setCancelable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= 26) {
                //在android7.1以上系统需要使用TYPE_PHONE类型 配合运行时权限
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else if (Build.VERSION.SDK_INT == 25) {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    public void showLoading(String msg){
        builder.setMessages(msg);
        mDialog = builder.create();
        Objects.requireNonNull(mDialog.getWindow()).setType(type);
        mDialog.show();
    }

    public void hideLoading(){
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    /**
     * 设置显示布局
     *
     * @return layout资源ID
     */
    protected abstract int setContentView();


    /**
     * 隐藏虚拟按键，并且全屏
     */
    @SuppressLint("ObsoleteSdkInt")
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
