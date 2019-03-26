package com.hospital.s1m.lib_base.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hospital.s1m.lib_base.data.SPDataSource;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by zouyifeng on 05/04/2018.
 * 11:50
 */

public class DeviceInfoUtils {

    private DeviceInfoUtils() {
    }

    /**
     * 一体机设备的SN号
     */
    private static String sn_number;

    /**
     * 获取商米设备的SN号
     *
     * @return 返回商米设备的SN号
     */
    public static String getDeviceSN() {
        try {

            if (TextUtils.isEmpty(sn_number)) {
                Class c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                Log.i("sunmi", "the sn:" + (String) get.invoke(c, "ro.serialno"));
                // Log.i("sunmi", "First four characters:" + (String) get.invoke(c, "ro.serialno").substring(0, 4));
                sn_number = (String) get.invoke(c, "ro.serialno");
            }


            return sn_number;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 设备类型为pad（平板）
     */
    public static final int DEVICE_TYPE_PAD = 0x0000000F;

    /**
     * 设备类型为phone（手机）
     */
    public static final int DEVICE_TYPE_PHONE = 0x000000F0;

    /**
     * 设备类型为SunMit（商米设备）
     */
    public static final int DEVICE_TYPE_SUN_MIT = 0x00000F00;

    /**
     * 获取设备当前类型，目前有
     * 商米设备 {@link #DEVICE_TYPE_SUN_MIT}
     * 平板设备 {@link #DEVICE_TYPE_PAD}
     * 手机设备 {@link #DEVICE_TYPE_PHONE}
     *
     * @param context 需要上下文获取设备信息
     * @return 返回设备类型
     */
    public static int getDeviceType(Context context) {

        boolean sunMiT = isSunMiT();

        //判断是否是商米设备
        if (sunMiT) {
            return DEVICE_TYPE_SUN_MIT;
        }

        //判断是否是平板设备，如果不是为手机设备
        if (isPad(context)) {
            return DEVICE_TYPE_PAD;
        } else {
            return DEVICE_TYPE_PHONE;
        }
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断当前设备是否是商米设备
     *
     * @return true 为商米一体机
     */
    public static boolean isSunMiT() {
        Log.w("Utils", Build.MANUFACTURER + " , " + Build.MODEL);
        return Build.MANUFACTURER != null && Build.MANUFACTURER.equals("SUNMI") && (Build.MODEL.contains("t1host"));
    }

    public static boolean isSunMiPos() {
        return Build.MANUFACTURER != null && Build.MANUFACTURER.equals("SUNMI") && (Build.MODEL.contains("V2") || Build.MODEL.equals("P1_4G"));
    }

    public static boolean isSunMiT1mini() {
        return Build.MANUFACTURER != null && Build.MANUFACTURER.equals("SUNMI") && (Build.MODEL.contains("T1mini"));
    }

    /**
     * 判断是否是双屏设备
     * -1 未检测 0 没有 1 有副屏
     */
    public static boolean isHasDoubleScreen(Context context) {
        int state = 0;
        try {
            state = (int) SPDataSource.get(context, "isDoubleScreen", -2);
            if (state == -2) {

                state = Settings.Global.getInt(context.getApplicationContext().getContentResolver(), "sunmi_sub");
                SPDataSource.put(context, "isDoubleScreen", state);
            }

            return state == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取设备IMEI号
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        if (null == context) {
            return null;
        }
        String imei = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            // android pad
            imei = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID).toString();
        }
        return imei;
    }


    /**
     * @param context
     * @return 此方法获取设备的唯一标识
     */
    public static String getDeviceNo(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();

        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; //13 digits

        String m_szAndroidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ; // Local Bluetooth adapter
        String m_szBTMAC = m_BluetoothAdapter.getAddress();

        String m_szLongID = m_szBTMAC + m_szAndroidID;
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF) {
                m_szUniqueID += "0";
            }
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }
}
