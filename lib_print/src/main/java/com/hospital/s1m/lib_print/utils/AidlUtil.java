package com.hospital.s1m.lib_print.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hospital.s1m.lib_print.ConfigureParams;
import com.hospital.s1m.lib_print.R;
import com.hospital.s1m.lib_base.constants.Formatter;
import com.hospital.s1m.lib_base.data.SPDataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public void printNumber(Context context, int number, String doctorName, String registrationId,
                            String sysUserId, int periodType, int registerType, String timea, String timeh, String timey, String wait) throws RemoteException {
        if (woyouService == null) {
            Toast.makeText(context, R.string.print_toast_2, Toast.LENGTH_LONG).show();
            return;
        }

        String period = "上午";
        if (periodType == 2) {
            period = "下午";
        } else if (periodType == 3) {
            period = "晚上";
        }

        String content = "id=" + registrationId + "&t=" + periodType;
        woyouService.lineWrap(1, null);
        woyouService.setAlignment(1, null);
        String clinic = (String) SPDataSource.get(context, "clinic", "");
//        String clinic = "致毉健康健康服務事業部測試診所";
        woyouService.printTextWithFont( clinic + "\n", null, 36, null);
        String time = Formatter.DATE_FORMAT0.format(new Date());
        woyouService.printTextWithFont(time + "\n", null, 24, null);
        woyouService.lineWrap(1, null);
        printBitmaps(AidlUtil.createNum(period, number));
        woyouService.lineWrap(1, null);
        woyouService.printTextWithFont("挂号医生: " + doctorName + "\n", null, 26, null);
        String waits = String.format("前面还有%s人在等待", wait);
        woyouService.printTextWithFont(waits + "\n", null, 26, null);
        woyouService.printTextWithFont("--------------------------------------\n", null, 20, null);
        woyouService.setAlignment(0, null);
        woyouService.printTextWithFont(" 温馨提示\n", null, 26, null);
        printBitmaps(AidlUtil.createTime(timea, timeh, timey));
        woyouService.setAlignment(1, null);
        woyouService.printTextWithFont("-------------------------------------\n", null, 20, null);
        if (registerType == 1) {
            printBitmaps(AidlUtil.createImage(content));
        }

        woyouService.lineWrap(6, null);
    }

    public void printBitmaps(Bitmap bitmap) {
        if (woyouService == null) {
            Toast.makeText(context, "服务已断开！", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.printBitmap(bitmap, null);
//            woyouService.lineWrap(3, null);
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

    public static Bitmap createNum(String period, int registrationNo) {
        int picWidth = 384;
        int picHeight = 85;
        int textColor = Color.BLACK;
        int textSize = 80;

        //最终生成的图片
        Bitmap result = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Paint paint2 = new Paint();
        paint.setColor(Color.WHITE);
        Canvas canvas = new Canvas(result);

        //先画一整块白色矩形块
        canvas.drawRect(0, 0, picWidth, picHeight, paint);

        //画title文字 34 80  String num, String st
        Rect bounds = new Rect();
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        Rect bound = new Rect();
        paint2.setColor(textColor);
        paint2.setTextSize(34);

        //获取文字的字宽高，以便将文字与图片中心对齐
        @SuppressLint("DefaultLocale")
        String s1 = String.format("%03d", registrationNo);
        String s2 = period;
        paint.getTextBounds(s1, 0, s1.length(), bounds);
        paint2.getTextBounds(s2, 0, s2.length(), bound);

        int tHeight = bounds.height();
        int tWidth1 = bounds.width();
        int tWidth2 = bound.width();
        int tw = tWidth1 + tWidth2;

        canvas.drawText(s1, picWidth / 2 - tw / 2 + tWidth2 + 5 , tHeight, paint);
        canvas.drawText(s2, picWidth / 2 - tw / 2 - 10, tHeight - 5, paint2);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return result;
    }

    public static Bitmap createTime(String timea, String timeh, String timey) {
        int picWidth = 384;
        int picHeight = 150;
        int textColor = Color.BLACK;
        int textSize = 24;

        //最终生成的图片
        Bitmap result = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Canvas canvas = new Canvas(result);

        //先画一整块白色矩形块
        canvas.drawRect(0, 0, picWidth, picHeight, paint);

        //画title文字
        Rect bounds = new Rect();
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        //获取文字的字宽高，以便将文字与图片中心对齐
        String s1 = " • 请您按照号码等待叫号就诊\n";
        String s2 = " • 过号请主动与医生护士联系\n";
        String titl = " • 出诊时间：";
        paint.getTextBounds(titl, 0, titl.length(), bounds);
        int tHeight = bounds.height();
        int tWidth = bounds.width();

        canvas.drawText(s1, 5, tHeight, paint);
        canvas.drawText(s2, 5, tHeight * 2  + 5, paint);
        canvas.drawText(titl, 5, tHeight * 3  + 10, paint);

        canvas.drawText(timea, tWidth + 15, tHeight * 3  + 10, paint);
        canvas.drawText(timeh, tWidth + 15, tHeight * 4  + 15, paint);
        canvas.drawText(timey, tWidth + 15, tHeight * 5  + 20, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return result;
    }

    public static Bitmap createImage(String content) {
        String title = "请您在候诊期间";
        String title2 = "扫描二维码填写个人信息";
        String url = "https://bt-clinicpe.yunzhenshi.com?" + content;
        int picWidth = 384;//生成图片的宽度
        int picHeight = 130;//生成图片的高度
        int titleTextSize = 20;
        int textColor = Color.BLACK;
        int qrWidth = 170;
        int qrHeight = 170;

        //最终生成的图片
        Bitmap result = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Canvas canvas = new Canvas(result);

        //先画一整块白色矩形块
        canvas.drawRect(0, 0, picWidth, picHeight, paint);

        //画title文字
        Rect bounds = new Rect();
        paint.setColor(textColor);
        paint.setTextSize(titleTextSize);
        //获取文字的字宽高，以便将文字与图片中心对齐
        paint.getTextBounds(title, 0, title.length(), bounds);
        int yy = picHeight / 2;
        int y2 = picHeight / 2 + bounds.height();
        canvas.drawText(title, 0, yy - 5, paint);
        canvas.drawText(title2, 0, y2 + 3, paint);

        //画二维码
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        BitMatrix bitMatrix;
        Bitmap image = null;
        try {
            bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
            bitMatrix = reduceWhite(bitMatrix, 0);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int imgYH = Objects.requireNonNull(image).getHeight();
        int padding = picHeight - imgYH;
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(Objects.requireNonNull(image), picWidth - picHeight, padding / 2, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return result;
    }

    /**
     * 缩小生成二维码白边框(删除白边 重新添加新白边)
     */
    private static BitMatrix reduceWhite(BitMatrix matrix, int margin) {
        int tempM = margin * 2;
        // 获取二维码图案的属性
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        // 按照自定义边框生成新的BitMatrix
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        // 循环，将二维码图案绘制到新的bitMatrix中
        for (int i = margin; i < resWidth - margin; i++) {
            for (int j = margin; j < resHeight - margin; j++) {
                if (matrix.get(i - margin + rec[0], j - margin + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

}
