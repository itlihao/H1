package com.zhiyihealth.registration.lib_base.utils;

/**
 * Created by Lihao on 2019-1-21.
 * Email heaolihao@163.com
 */
public class Utils {
    private static final int MIN_DELAY_TIME = 1000;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        LogUtils.w("SoftKeyC", "快速点击:" + flag);
        return flag;
    }
}
