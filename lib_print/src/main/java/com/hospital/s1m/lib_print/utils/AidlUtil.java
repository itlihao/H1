package com.hospital.s1m.lib_print.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.hospital.s1m.lib_print.ConfigureParams;
import com.hospital.s1m.lib_print.R;
import com.hospital.s1m.lib_print.bean.TableItem;
import com.zhiyihealth.registration.lib_base.constants.Formatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;


public class AidlUtil {
    private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
    private static final String SERVICE＿ACTION = "woyou.aidlservice.jiuiv5.IWoyouService";

    private IWoyouService woyouService;
    private static AidlUtil mAidlUtil = new AidlUtil();
    private Context context;

    private AidlUtil() {
    }

    public static AidlUtil getInstance() {
        return mAidlUtil;
    }

    /**
     * 连接服务
     *
     * @param context context
     */
    public void connectPrinterService(Context context) {
        this.context = context.getApplicationContext();
        Intent intent = new Intent();
        intent.setPackage(SERVICE＿PACKAGE);
        intent.setAction(SERVICE＿ACTION);
        context.getApplicationContext().startService(intent);
        context.getApplicationContext().bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    /**
     * 断开服务
     *
     * @param context context
     */
    public void disconnectPrinterService(Context context) {
        if (woyouService != null) {
            context.getApplicationContext().unbindService(connService);
            woyouService = null;
        }
    }

    public boolean isConnect() {
        return woyouService != null;
    }

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    public ICallback generateCB(final PrinterCallback printerCallback) {
        return new ICallback.Stub() {


            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                printerCallback.onReturnString(result);
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        };
    }

    /**
     * 设置打印浓度
     */
    private int[] darkness = new int[]{0x0600, 0x0500, 0x0400, 0x0300, 0x0200, 0x0100, 0,
            0xffff, 0xfeff, 0xfdff, 0xfcff, 0xfbff, 0xfaff};

    public void setDarkness(int index) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        int k = darkness[index];
        try {
            woyouService.sendRAWData(ESCUtil.setPrinterDarkness(k), null);
            woyouService.printerSelfChecking(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得打印机系统信息，放在list中
     *
     * @return list
     */
    public List<String> getPrinterInfo(PrinterCallback printerCallback1, PrinterCallback printerCallback2) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return null;
        }

        List<String> info = new ArrayList<>();
        try {
            woyouService.getPrintedLength(generateCB(printerCallback1));
            woyouService.getPrinterFactory(generateCB(printerCallback2));
            info.add(woyouService.getPrinterSerialNo());
            info.add(woyouService.getPrinterModal());
            info.add(woyouService.getPrinterVersion());
            info.add(printerCallback1.getResult());
            info.add(printerCallback2.getResult());
            //info.add(woyouService.getServiceVersion());
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(SERVICE＿PACKAGE, 0);
                if (packageInfo != null) {
                    info.add(packageInfo.versionName);
                    info.add(packageInfo.versionCode + "");
                } else {
                    info.add("");
                    info.add("");
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 初始化打印机
     */
    public void initPrinter() {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.printerInit(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印二维码
     */
    public void printQr(String data, String clinicName, String doctorName) throws RemoteException {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }
        woyouService.setAlignment(1, null);
        woyouService.setFontSize(24, null);
        woyouService.printTextWithFont(clinicName + "\n", "", 35, null);
        woyouService.lineWrap(1, null);
        woyouService.printTextWithFont(doctorName + "医生的专属二维码" + "\n", "", 25, null);
        woyouService.lineWrap(1, null);
        woyouService.printQRCode(data, 5, 2, null);
        woyouService.setAlignment(0, null);
        woyouService.lineWrap(1, null);
        woyouService.printTextWithFont(ConfigureParams.CODE_TITLE, "", 28, null);
        woyouService.printTextWithFont(ConfigureParams.CODE_CONTENT_1, "", 25, null);
        woyouService.printTextWithFont("　 二维码；\n", "", 25, null);
        woyouService.printTextWithFont(ConfigureParams.CODE_CONTENT_2, "", 25, null);
        woyouService.printTextWithFont(ConfigureParams.CODE_CONTENT_3, "", 25, null);
        woyouService.printTextWithFont(ConfigureParams.CODE_CONTENT_4, "", 25, null);
        woyouService.lineWrap(2, null);
    }

    /**
     * 打印条形码
     */
    public void printBarCode(String data, int symbology, int height, int width, int textposition) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }


        try {
            woyouService.printBarCode(data, symbology, height, width, textposition, null);
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印文字
     */
    public void printText(String content, float size, boolean isBold, boolean isUnderLine) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            if (isBold) {
                woyouService.sendRAWData(ESCUtil.boldOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if (isUnderLine) {
                woyouService.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.underlineOff(), null);
            }

            woyouService.printTextWithFont(content, null, size, null);
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void printNumber(String number, String patientName, String doctorName, String registerDate, int needWait) throws RemoteException {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        woyouService.lineWrap(1, null);
        woyouService.setAlignment(1, null);
        woyouService.printTextWithFont("致医健康未来诊所\n", null, 37, null);
        String time = Formatter.DATE_FORMAT0.format(new Date());
        woyouService.printTextWithFont(time + "\n", null, 23, null);
        woyouService.lineWrap(1, null);
        woyouService.printTextWithFont(" 下午\n", null, 34, null);
        woyouService.printTextWithFont("022", null, 80, null);
        woyouService.lineWrap(1, null);
        woyouService.printTextWithFont("挂号医生: 李时珍\n", null, 26, null);
        woyouService.printTextWithFont("-----------------------------\n", null, 26, null);
        woyouService.setAlignment(0, null);
        woyouService.printTextWithFont("  温馨提示\n", null, 26, null);
        woyouService.printTextWithFont("  • 请您按照号码等待叫号就诊\n", null, 23, null);
        woyouService.printTextWithFont("  • 过号请主动与医生护士联系\n", null, 23, null);
        woyouService.printTextWithFont("  • 出诊时间：\n", null, 23, null);
        woyouService.setAlignment(1, null);
        woyouService.printTextWithFont("上午 08:00-12:00\n", null, 23, null);
        woyouService.printTextWithFont("下午 14:00-18:00\n", null, 23, null);
        woyouService.printTextWithFont("晚上 14:00-18:00\n", null, 23, null);
        woyouService.printTextWithFont("-----------------------------\n", null, 26, null);
        woyouService.setAlignment(0, null);
        woyouService.printTextWithFont("  请您在候诊期间\n", null, 20, null);
        woyouService.printTextWithFont("  扫描二维码填写个人信息\n", null, 20, null);
        woyouService.lineWrap(1, null);
        woyouService.setAlignment(1, null);
        woyouService.printQRCode("https://www.baidu.com/", 5, 2, null);
//        print3Line();

        LinkedList<TableItem> tableItems = new LinkedList<>();

        if(needWait>0){
            TableItem tableItem0 = new TableItem();
            tableItem0.setText(new String[]{"等候人数:", needWait+""});
            tableItem0.setWidth(new int[]{1, 1});
            tableItem0.setAlign(new int[]{2, 0});
            tableItems.add(tableItem0);
        }

        TableItem tableItem = new TableItem();
        tableItem.setText(new String[]{"就诊序号:", number});
        tableItem.setWidth(new int[]{1, 1});
        tableItem.setAlign(new int[]{2, 0});

        TableItem tableItem1 = new TableItem();
        tableItem1.setText(new String[]{"患者姓名:", patientName});
        tableItem1.setWidth(new int[]{1, 1});
        tableItem1.setAlign(new int[]{2, 0});

        TableItem tableItem2 = new TableItem();
        tableItem2.setText(new String[]{"医生姓名:", doctorName});
        tableItem2.setWidth(new int[]{1, 1});
        tableItem2.setAlign(new int[]{2, 0});

        TableItem tableItem3 = new TableItem();
        tableItem3.setText(new String[]{"挂号时间:", registerDate});
        tableItem3.setWidth(new int[]{1, 1});
        tableItem3.setAlign(new int[]{2, 0});

        tableItems.add(tableItem);
        tableItems.add(tableItem1);
        tableItems.add(tableItem2);
        tableItems.add(tableItem3);
//        printTable(tableItems);

//        print3Line();
        woyouService.lineWrap(6, null);
    }

    /**
     * 打印表格
     */
    public void printTable(LinkedList<TableItem> list) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            for (TableItem tableItem : list) {
                woyouService.printColumnsString(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign(), null);
            }
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
     *打印图片
     */
    public void printBitmap(Bitmap bitmap) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.setAlignment(1, null);
            woyouService.printBitmap(bitmap, null);
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 打印图片和文字按照指定排列顺序
     */
    public void printBitmap(Bitmap bitmap, int orientation) {
        if (woyouService == null) {
            Toast.makeText(context, "服务已断开！", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            if (orientation == 0) {
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("横向排列\n", null);
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("横向排列\n", null);
            } else {
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("\n纵向排列\n", null);
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("\n纵向排列\n", null);
            }
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
     * 空打三行！
     */
    public void print3Line() {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void sendRawData(byte[] data) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.sendRAWData(data, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendRawDatabyBuffer(byte[] data, ICallback iCallback) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.enterPrinterBuffer(true);
            woyouService.sendRAWData(data, iCallback);
            woyouService.exitPrinterBufferWithCallback(true, iCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
