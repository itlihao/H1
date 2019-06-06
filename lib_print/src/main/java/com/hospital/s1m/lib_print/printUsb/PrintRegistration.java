package com.hospital.s1m.lib_print.printUsb;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.billy.cc.core.component.CC;
import com.hospital.s1m.lib_base.base.BaseActivity;
import com.hospital.s1m.lib_base.utils.ToastUtils;

import java.util.HashMap;

/**
 * Created by Lihao on 2019-5-10.
 * Email heaolihao@163.com
 */
public class PrintRegistration {
    private static PrintRegistration single = null;
    private BaseActivity mActivity;

    private CC mCc;

    private PrinterInstance myPrinter;
    private UsbDevice mUSBDevice;
    private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";

    public static PrintRegistration getInstance() {
        if (single == null) {
            single = new PrintRegistration();
        }

        return single;
    }

    public void initPrint(CC cc) {
        BaseActivity activity = cc.getParamItem("activity");
        mCc = cc;

        mActivity = activity;
        doDiscovery();
    }

    private void printUsb(CC cc) {
        int number = cc.getParamItem("number");
        String doctorName = cc.getParamItem("doctorName");
        String registrationId = cc.getParamItem("registrationId");
        int registerType = cc.getParamItem("registerType");
        int periodType = cc.getParamItem("periodType");
        String timea = cc.getParamItem("timeS1");
        String timeh = cc.getParamItem("timeS2");
        String timey = cc.getParamItem("timeS3");
        String wait = cc.getParamItem("wait");
        BaseActivity activity = cc.getParamItem("activity");
        UsbPrintUtil.getInstance().printUsb(PrinterHelper.getInstance().getPrinterInstanceUSB(), activity,
                number, doctorName, registrationId, periodType, registerType, timea, timeh, timey, wait);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    System.out.println("SUCCESS");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(myPrinter);
                    printUsb(mCc);
                    /*listener.setCancle();
                    dismiss();*/
                    break;
                case PrinterConstants.Connect.FAILED:
                    System.out.println("FAILED");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(null);
                    break;
                case PrinterConstants.Connect.CLOSED:
                    System.out.println("CLOSED");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(null);
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    System.out.println("NODEVICE");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(null);
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private void doDiscovery() {
        UsbManager manager = (UsbManager) mActivity.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        if (devices.size() == 0) {
            ToastUtils.showToast(mActivity, "请连接打印机！");
            return;
        }
        for (UsbDevice device : devices.values()) {
            //只显示打印机
            UsbInterface anInterface = device.getInterface(0);
            if (7 == anInterface.getInterfaceClass()) {
                /*deviceArrayAdapter.add(device.getDeviceName() + "\nvid: "
                        + device.getVendorId() + " pid: "
                        + device.getProductId());*/
                if (device.getVendorId() == 1155 && device.getProductId() == 22304) {
//                    deviceList.add(device);
                    mUSBDevice = device;
                    returnToPreviousActivity(mUSBDevice);
                    return;
                } else {
                    mUSBDevice = null;
                    ToastUtils.showToast(mActivity, "请连接打印机！");
                }
            } else {
                ToastUtils.showToast(mActivity, "请连接打印机！");
            }
        }
    }

    private void returnToPreviousActivity(UsbDevice mUSBDevice) {
        myPrinter = new PrinterInstance(mActivity, mUSBDevice, mHandler);
        UsbManager mUsbManager = (UsbManager) mActivity.getSystemService(Context.USB_SERVICE);
        if (mUsbManager.hasPermission(mUSBDevice)) {
            myPrinter.openConnection();
        } else {
            // 没有权限询问用户是否授予权限
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            mActivity.registerReceiver(mUsbReceiver, filter);
            // 该代码执行后，系统弹出一个对话框
            mUsbManager.requestPermission(mUSBDevice, pendingIntent);
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        @SuppressLint("NewApi")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    mActivity.unregisterReceiver(mUsbReceiver);
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUSBDevice.equals(device)) {
                        myPrinter.openConnection();
                    } else {
                        mHandler.obtainMessage(PrinterConstants.Connect.FAILED).sendToTarget();
                    }
                }
            }
        }
    };
}
