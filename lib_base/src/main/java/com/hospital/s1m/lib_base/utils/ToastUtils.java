package com.hospital.s1m.lib_base.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 单例Toast
 * Created by yalong on 2016/6/22.
 */
public class ToastUtils {
    private static String oldMsg;
    private static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(Context context, @StringRes int resId) {
        showToast(context, context.getString(resId));
    }

    public static void showToast(Context context, String msg) {
        if (msg == null) {
            return;
        }
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (msg.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = msg;
                toast.setText(msg);
                toast.show();
            }
        }
        oneTime = twoTime;
    }
}
