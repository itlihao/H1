package com.hospital.s1m.lib_print.printUsb;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.hospital.s1m.lib_base.base.BaseActivity;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_print.utils.AidlUtil;

/**
 * Created by Lihao on 2019-5-10.
 * Email heaolihao@163.com
 */
public class UsbPrintUtil {
    private static UsbPrintUtil single = null;
    private PrinterInstance mPrinter;

    private UsbPrintUtil() {

    }

    public static UsbPrintUtil getInstance() {
        if (single == null) {
            single = new UsbPrintUtil();
        }

        return single;
    }


    void printUsb(PrinterInstance printer, BaseActivity activity, int number, String doctorName, String registrationId,
                  int periodType, int registerType, String timea, String timeh, String timey, String wait) {
        mPrinter = printer;
        mPrinter.init();
        setWidth();

        String period = "上午";
        if (periodType == 2) {
            period = "下午";
        } else if (periodType == 3) {
            period = "晚上";
        }

        String content = "id=" + registrationId + "&t=" + periodType;
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 0);
        // String clinic = "致毉健康健康服務事業部測試診所";

        String clinic = (String) SPDataSource.get(activity, "clinic", "");
        mPrinter.printImage(AidlUtil.createImg(clinic));
        mPrinter.printText("\n");
        mPrinter.printImage(AidlUtil.createNum1(period, number));
        mPrinter.printImage(AidlUtil.createDoc(doctorName, wait));
        mPrinter.printText("\n");
        mPrinter.printText("-----------------------------------------------\n");
        mPrinter.printImage(AidlUtil.createTime1(timea, timeh, timey));
        mPrinter.printText("-----------------------------------------------\n");
        if (registerType == 1) {
            mPrinter.printImage(AidlUtil.createImage1(content));
        }
        mPrinter.cutPaper();
    }

    private void setWidth() {
        mPrinter.setLeftMargin(8, 0);
        mPrinter.sendByteData(new byte[]{29, 87, 48, 3});
    }
}
