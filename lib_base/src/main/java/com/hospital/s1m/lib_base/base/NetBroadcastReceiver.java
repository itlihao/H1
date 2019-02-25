package com.hospital.s1m.lib_base.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.hospital.s1m.lib_base.utils.NetworkUtils;

import java.util.Objects;

/**
 * Created by Lihao on 2019-2-19.
 * Email heaolihao@163.com
 */
public class NetBroadcastReceiver extends BroadcastReceiver {

    public NetChangeListener listener = BaseActivity.listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 如果相等的话就说明网络状态发生了变化
        Log.i("NetBroadcastReceiver", "NetBroadcastReceiver changed");
        if (Objects.requireNonNull(intent.getAction()).equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetworkUtils.getNetWorkState(context);
            // 当网络发生变化，判断当前网络状态，并通过NetEvent回调当前网络状态
            if (listener != null) {
                listener.onChangeListener(netWorkState);
            }
        }
    }

    // 自定义接口
    public interface NetChangeListener {
        void onChangeListener(int status);
    }

}