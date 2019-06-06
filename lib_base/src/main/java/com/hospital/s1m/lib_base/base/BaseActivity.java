package com.hospital.s1m.lib_base.base;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.hospital.s1m.lib_base.utils.Logger;
import com.hospital.s1m.lib_base.utils.NetworkUtils;
import com.hospital.s1m.lib_base.view.MDDialog;

import java.util.Objects;

/**
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public abstract class BaseActivity extends RxAppCompatActivity implements NetBroadcastReceiver.NetChangeListener {
    public MDDialog mDialog;

    public MDDialog.Builder builder;

    public static NetBroadcastReceiver.NetChangeListener listener;

    NetBroadcastReceiver netBroadcastReceiver;

    /**
     * 网络类型
     */
    public int netType;

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

        listener = this;
        //Android 7.0以上需要动态注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //实例化IntentFilter对象
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new NetBroadcastReceiver();
            //注册广播接收
            registerReceiver(netBroadcastReceiver, filter);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netBroadcastReceiver != null) {
            unregisterReceiver(netBroadcastReceiver);
        }
    }

    /**
     * 初始化时判断有没有网络
     */
    public void checkNet() {
        this.netType = NetworkUtils.getNetWorkState(BaseActivity.this);
        if (!isNetConnect()) {
            //网络异常，请检查网络
            Logger.d("BaseActivity", "网络异常，请检查网络");
        }
        isNetConnect();
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onChangeListener(int netMobile) {
        netType = netMobile;
        if (!isNetConnect()) {
            Logger.d("BaseActivity", "网络异常，请检查网络");
        } else {
            Logger.d("BaseActivity", "网络恢复正常");
        }
    }

    /**
     * 判断有无网络 。
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netType == 1) {
            return true;
        } else if (netType == 0) {
            return true;
        } else if (netType == 2) {
            return true;
        } else if (netType == -1) {
            return false;
        }
        return false;
    }


    public void showLoading(String msg) {
        if (mDialog != null) {
            return;
        }
        builder.setMessages(msg);
        mDialog = builder.create();
        Objects.requireNonNull(mDialog.getWindow()).setType(type);
        mDialog.show();
    }

    public void hideLoading() {
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
