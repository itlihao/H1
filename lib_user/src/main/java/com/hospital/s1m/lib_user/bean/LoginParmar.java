package com.hospital.s1m.lib_user.bean;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hospital.s1m.lib_base.utils.DeviceInfoUtils;

public class LoginParmar {

    /**
     * device : {"branch":"MI PAD 2","brand":"Xiaomi","clientMac":"","deviceName":"MI PAD 2","deviceNo":"A3P44A1849F6","deviceType":"3","ratio":"2048x1536","sysVersion":"5.1"}
     * loginName : panda2
     * password : 96e79218965eb72c92a549dd5a330112
     */

    private DeviceBean device;
    private String loginName;
    private String password;
    private boolean forget;

    public LoginParmar(String loginName, String password, boolean forget) {
        this.loginName = loginName;
        this.password = password;
        this.forget = forget;
    }

    public LoginParmar(Context context, String loginName, String password, boolean forget) {
        this.loginName = loginName;
        this.password = password;
        this.forget = forget;
        device = DeviceBean.generateDeviceInfoDTO(context);
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isForget() {
        return forget;
    }

    public void setForget(boolean forget) {
        this.forget = forget;
    }

    public static class DeviceBean {
        /**
         * 机型
         */
        private String branch = "";

        /**
         * 厂商
         */
        private String brand = "";

        /**
         * mac地址
         */
        private String clientMac = "";

        /**
         * 设备名称
         */
        private String deviceName = "";

        /**
         * 设备号
         */
        private String deviceNo = "";

        /**
         * 设备类型 1一体机 2加密狗
         */
        private String deviceType = "";

        /**
         * 分辨率
         */
        private String ratio = "";

        /**
         * 系统版本
         */
        private String sysVersion = "";

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getClientMac() {
            return clientMac;
        }

        public void setClientMac(String clientMac) {
            this.clientMac = clientMac;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceNo() {
            return deviceNo;
        }

        public void setDeviceNo(String deviceNo) {
            this.deviceNo = deviceNo;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getRatio() {
            return ratio;
        }

        public void setRatio(String ratio) {
            this.ratio = ratio;
        }

        public String getSysVersion() {
            return sysVersion;
        }

        public void setSysVersion(String sysVersion) {
            this.sysVersion = sysVersion;
        }

        public static DeviceBean generateDeviceInfoDTO(Context context) {
            DeviceBean deviceInfoDTO = new DeviceBean();
            deviceInfoDTO.setDeviceName(android.os.Build.MODEL);
            deviceInfoDTO.setDeviceType(DeviceInfoUtils.isSunMiT() ? "1" : "3");
            deviceInfoDTO.setBranch(android.os.Build.MODEL);
            deviceInfoDTO.setBrand(android.os.Build.MANUFACTURER);
            deviceInfoDTO.setClientMac("");
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            deviceInfoDTO.setRatio(width + "x" + height);
            deviceInfoDTO.setSysVersion(android.os.Build.VERSION.RELEASE);
            return deviceInfoDTO;
        }
    }
}
