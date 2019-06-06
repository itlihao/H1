package com.hospital.s1m.lib_print.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.hospital.s1m.lib_base.constants.Formatter;
import com.hospital.s1m.lib_base.constants.Urls;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_print.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import woyou.aidlservice.jiuiv5.IWoyouService;


/**
 * @author Lihao
 */
public class AidlUtil {
    private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
    private static final String SERVICE＿ACTION = "woyou.aidlservice.jiuiv5.IWoyouService";

    private IWoyouService woyouService;
    @SuppressLint("StaticFieldLeak")
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


    /**
     * 58mm打印单
     *
     * @param context        context
     * @param number         序号
     * @param doctorName     医生名字
     * @param registrationId 挂号ID
     * @param sysUserId      医生ID
     * @param periodType     时间类型 123对应上午、下午、晚上
     * @param registerType   挂号类型 1快速挂号
     * @param timea          上午营业时间
     * @param timeh          下午营业时间
     * @param timey          晚上营业时间
     * @param wait           等待人数
     * @throws RemoteException 异常信息
     */
    public void printForm58(Context context, int number, String doctorName, String registrationId,
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
        woyouService.printTextWithFont(clinic + "\n", null, 36, null);
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

    /**
     * 80mm打印单
     *
     * @param context        context
     * @param number         序号
     * @param doctorName     医生名字
     * @param registrationId 挂号ID
     * @param periodType     时间类型 123对应上午、下午、晚上
     * @param registerType   挂号类型 1快速挂号
     * @param timea          上午营业时间
     * @param timeh          下午营业时间
     * @param timey          晚上营业时间
     * @param wait           等待人数
     * @throws RemoteException 异常信息
     */
    public void printForm80(Context context, int number, String doctorName, String registrationId,
                            int periodType, int registerType, String timea, String timeh, String timey, String wait) throws RemoteException {
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
        woyouService.printTextWithFont(clinic + "\n", null, 49, null);
        woyouService.lineWrap(1, null);
        String time = Formatter.DATE_FORMAT0.format(new Date());
        woyouService.printTextWithFont(time + "\n", null, 33, null);
        woyouService.lineWrap(1, null);
        printBitmaps(AidlUtil.createNum1(period, number));
        woyouService.lineWrap(1, null);
        woyouService.printTextWithFont("挂号医生: " + doctorName + "\n", null, 36, null);
        woyouService.lineWrap(1, null);
        String waits = String.format("前面还有%s人在等待", wait);
        woyouService.printTextWithFont(waits + "\n", null, 36, null);
        woyouService.printTextWithFont("--------------------------------------\n", null, 28, null);
        woyouService.lineWrap(1, null);
        woyouService.setAlignment(0, null);
        printBitmaps(AidlUtil.createTime1(timea, timeh, timey));
        woyouService.lineWrap(1, null);
        woyouService.setAlignment(1, null);
        woyouService.printTextWithFont("-------------------------------------\n", null, 28, null);
        if (registerType == 1) {
            printBitmaps(AidlUtil.createImage1(content));
        }

        woyouService.lineWrap(2, null);
        woyouService.cutPaper(null);
    }


    private void printBitmaps(Bitmap bitmap) {
        if (woyouService == null) {
            Toast.makeText(context, "服务已断开！", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.printBitmap(bitmap, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成挂号序号图片 80mm
     *
     * @param period         时间类型
     * @param registrationNo 序号
     * @return 图片
     */
    public static Bitmap createNum1(String period, int registrationNo) {
        int picWidth = 530;
        int picHeight = 117;
        int textColor = Color.BLACK;
        int textSize = 110;

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
        paint2.setTextSize(47);

        //获取文字的字宽高，以便将文字与图片中心对齐
        @SuppressLint("DefaultLocale")
        String s1 = String.format("%03d", registrationNo);
        paint.getTextBounds(s1, 0, s1.length(), bounds);
        paint2.getTextBounds(period, 0, period.length(), bound);

        int tHeight = bounds.height();
        int tWidth1 = bounds.width();
        int tWidth2 = bound.width();
        int tw = tWidth1 + tWidth2;

        canvas.drawText(s1, picWidth / 2 - tw / 2 + tWidth2 + 5, tHeight, paint);
        canvas.drawText(period, picWidth / 2 - tw / 2 - 10, tHeight - 5, paint2);

        canvas.save();
//        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return result;
    }

    /**
     * 生成挂号序号图片 58mm
     *
     * @param period         时间类型
     * @param registrationNo 序号
     * @return 图片
     */
    private static Bitmap createNum(String period, int registrationNo) {
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
        paint.getTextBounds(s1, 0, s1.length(), bounds);
        paint2.getTextBounds(period, 0, period.length(), bound);

        int tHeight = bounds.height();
        int tWidth1 = bounds.width();
        int tWidth2 = bound.width();
        int tw = tWidth1 + tWidth2;

        canvas.drawText(s1, picWidth / 2 - tw / 2 + tWidth2 + 5, tHeight, paint);
        canvas.drawText(period, picWidth / 2 - tw / 2 - 10, tHeight - 5, paint2);

        canvas.save();
        canvas.restore();
        return result;
    }

    /**
     * 生成营业时间图片 80mm
     *
     * @param timea 上午
     * @param timeh 下午
     * @param timey 晚上
     * @return 图片
     */
    public static Bitmap createTime1(String timea, String timeh, String timey) {
        int picWidth = 530;
        int picHeight = 233;
        int textColor = Color.BLACK;
        int textSize = 33;

        //最终生成的图片
        Bitmap result = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Canvas canvas = new Canvas(result);

        //先画一整块白色矩形块
        canvas.drawRect(0, 0, picWidth, picHeight, paint);

        //画title文字

        paint.setColor(textColor);

        //获取文字的字宽高，以便将文字与图片中心对齐
        String s0 = "温馨提示\n";
        String s1 = " • 请您按照号码等待叫号就诊\n";
        String s2 = " • 过号请主动与医生护士联系\n";
        String title = " • 营业时间：";

        Rect bound = new Rect();
        paint.setTextSize(36);
        paint.getTextBounds(s0, 0, s0.length(), bound);
        int h0 = bound.height() + 10, w0 = bound.width();
        canvas.drawText(s0, 10, h0 - 10, paint);

        Rect bounds = new Rect();
        paint.setTextSize(textSize);
        paint.getTextBounds(title, 0, title.length(), bounds);
        int tHeight = bounds.height();
        int tWidth = bounds.width();

        canvas.drawText(s1, 5, tHeight + h0 + 5, paint);
        canvas.drawText(s2, 5, tHeight * 2 + 10 + h0, paint);
        canvas.drawText(title, 5, tHeight * 3 + 15 + h0, paint);

        canvas.drawText(timea, tWidth + 15, tHeight * 3 + 15 + h0, paint);
        canvas.drawText(timeh, tWidth + 15, tHeight * 4 + 20 + h0, paint);
        canvas.drawText(timey, tWidth + 15, tHeight * 5 + 25 + h0, paint);

        canvas.save();
        canvas.restore();
        return result;
    }

    private static Bitmap createTime(String timea, String timeh, String timey) {
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
        String title = " • 营业时间：";
        paint.getTextBounds(title, 0, title.length(), bounds);
        int tHeight = bounds.height();
        int tWidth = bounds.width();

        canvas.drawText(s1, 5, tHeight, paint);
        canvas.drawText(s2, 5, tHeight * 2 + 5, paint);
        canvas.drawText(title, 5, tHeight * 3 + 10, paint);

        canvas.drawText(timea, tWidth + 15, tHeight * 3 + 10, paint);
        canvas.drawText(timeh, tWidth + 15, tHeight * 4 + 15, paint);
        canvas.drawText(timey, tWidth + 15, tHeight * 5 + 20, paint);

        canvas.save();
        canvas.restore();
        return result;
    }

    /**
     * 生成挂号二维码图片 58mm
     *
     * @param content 二维码内容
     * @return 二维码图片
     */
    private static Bitmap createImage(String content) {
        String title = "请您在候诊期间";
        String title2 = "扫描二维码填写个人信息";
        String url = Urls.QRURL + "?" + content;
        //生成图片的宽度
        int picWidth = 384;
        //生成图片的高度
        int picHeight = 130;
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

        canvas.save();
        canvas.restore();
        return result;
    }

    /**
     * 生成挂号二维码图片 80mm
     *
     * @param content 二维码内容
     * @return 二维码图片
     */
    public static Bitmap createImage1(String content) {
        String title = "请您在候诊期间";
        String title2 = "扫描二维码填写个人信息";
        String url = Urls.QRURL + "?" + content;
        int picWidth = 560;//生成图片的宽度
        int picHeight = 179;//生成图片的高度
        int titleTextSize = 28;
        int textColor = Color.BLACK;
        int qrWidth = 235;
        int qrHeight = 235;

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
        canvas.drawBitmap(Objects.requireNonNull(image), picWidth - picHeight - 5, padding / 2, paint);

        canvas.save();
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

    public static Bitmap createImg(String clinic) {
        int picWidth = 560;
        int picHeight = 120;
        int textColor = Color.BLACK;
        int textSize = 49;

        //最终生成的图片
        Bitmap result = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Canvas canvas = new Canvas(result);

        //先画一整块白色矩形块
        canvas.drawRect(0, 0, picWidth, picHeight, paint);

        //画title文字
        Rect bound1 = new Rect();
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        //获取文字的字宽高，以便将文字与图片中心对齐
        String s1 = clinic + "\n";
        paint.getTextBounds(s1, 0, s1.length(), bound1);
        int w1 = bound1.width(), h1 = bound1.height();
        canvas.drawText(s1, (picWidth - w1) / 2, 5 + h1, paint);

        Rect bound2 = new Rect();
        paint.setTextSize(33);
        String s2 = Formatter.DATE_FORMAT0.format(new Date()) + "\n";
        paint.getTextBounds(s2, 0, s2.length(), bound2);
        int w2 = bound2.width(), h2 = bound2.height();
        canvas.drawText(s2, (picWidth - w2) / 2, 5 + h1 + 30 + h2, paint);

        canvas.save();
        canvas.restore();
        return result;
    }

    public static Bitmap createDoc(String doc, String wait) {
        int picWidth = 560;
        int picHeight = 111;
        int textColor = Color.BLACK;

        //最终生成的图片
        Bitmap result = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Canvas canvas = new Canvas(result);

        //先画一整块白色矩形块
        canvas.drawRect(0, 0, picWidth, picHeight, paint);

        Rect bound3 = new Rect();
        paint.setColor(textColor);
        paint.setTextSize(36);
        String s3 = "挂号医生：" + doc;
        paint.getTextBounds(s3, 0, s3.length(), bound3);
        int w3 = bound3.width(), h3 = bound3.height();
        canvas.drawText(s3, (picWidth - w3) / 2, 5 + h3, paint);

        Rect bound4 = new Rect();
        String s4 = String.format("前面还有%s人在等待", wait);
        paint.getTextBounds(s4, 0, s4.length(), bound4);
        int w4 = bound4.width(), h4 = bound4.height();
        canvas.drawText(s4, (picWidth - w4) / 2, 5 + h3 + 30 + h4, paint);

        canvas.save();
        canvas.restore();
        return result;
    }

}
