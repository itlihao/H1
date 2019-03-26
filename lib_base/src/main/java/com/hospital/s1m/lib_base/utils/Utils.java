package com.hospital.s1m.lib_base.utils;

import java.util.Calendar;

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

    public static int getAgeFromBirthTime(String birthTimeString) {
        // 先截取到字符串中的年、月、日
        birthTimeString = birthTimeString.replaceAll("–", "-");
        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        // 先大致赋值
        int age = yearMinus;
        // 选了未来的年份
        if (yearMinus < 0) {
            age = 0;
        } else if (yearMinus == 0) {// 同年的，要么为1，要么为0
            if (monthMinus < 0) {// 选了未来的月份
                age = 0;
            } else if (monthMinus == 0) {// 同月份的
                if (dayMinus < 0) {// 选了未来的日期
                    age = 0;
                } else {
                    age = 1;
                }
            } else {
                age = 1;
            }
        } else {
            if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
                if (dayMinus < 0) {

                } else {
                    age = age + 1;
                }
            } else {
                age = age + 1;
            }
        }
        return age;
    }

    public static int getPeriodType() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int type = 0;
        if (hour > 0 && hour < 12) {
            type = 1;
        } else if (hour >= 12 && hour < 18) {
            type = 2;
        } else if (hour >= 18 && hour < 24) {
            type = 3;
        }
        return type;
    }
}
