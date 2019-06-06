package com.hospital.s1m.lib_print.printUsb;

import com.android.print.sdk.PrinterInstance;

/**
 * Author:zhangzhennan
 * Time:2018/10/17
 * Description:打印
 */
public class PrinterHelper {
    private PrinterHelper() {
    }

    private static PrinterHelper single = null;

    //静态工厂方法
    public static PrinterHelper getInstance() {
        if (single == null) {
            single = new PrinterHelper();
        }
        return single;
    }


    private PrinterInstance printerInstanceUSB;

    PrinterInstance getPrinterInstanceUSB() {
        if (printerInstanceUSB != null) {
            printerInstanceUSB.setLeftMargin(40, 0);
            printerInstanceUSB.sendByteData(new byte[]{29, 87, 20, 3});
        }
        return printerInstanceUSB;
    }

    void setPrinterInstanceUSB(PrinterInstance printerInstanceUSB) {
        if (this.printerInstanceUSB != null) {
            this.printerInstanceUSB.closeConnection();
        }
        this.printerInstanceUSB = printerInstanceUSB;
    }

}
