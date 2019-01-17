package registration.zhiyihealth.com.lib_ime.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import java.util.List;

import registration.zhiyihealth.com.lib_ime.R;

import static registration.zhiyihealth.com.lib_ime.view.SoftKeyContainer.KEYCODE_CLOSE;
import static registration.zhiyihealth.com.lib_ime.view.SoftKeyContainer.KEYCODE_SWITCH;

/**
 * @author Lihao
 * @date 2019-1-4
 * Email heaolihao@163.com
 */
public class MyKeyboardView extends KeyboardView {

    private static final String TAG = "MyKeyboardView";

    private Context mContext;
    private boolean isCap;
    private boolean isCN;
    private Drawable delDrawable;
    private Drawable lowDrawable;
    private Drawable upDrawable;
    /**
     * 按键的宽高至少是图标宽高的倍数
     */
    private static final int ICON2KEY = 2;

    private static final int HORIZONTAL_GAP = 10;

    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.mContext = context;
    }

    public MyKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        this.mContext = context;
    }

    private void init() {
        this.isCap = false;
        this.isCN = true;
        this.delDrawable = null;
        this.lowDrawable = null;
        this.upDrawable = null;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == Keyboard.KEYCODE_DELETE || key.codes[0] == Keyboard.KEYCODE_SHIFT
                        || key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE || key.codes[0] == KEYCODE_SWITCH
                        || key.codes[0] == KEYCODE_CLOSE) {
                    drawSpecialKey(canvas, key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawSpecialKey(Canvas canvas, Keyboard.Key key) {
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.keyboard_change);
        if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
            drawKeyBackground(drawable, canvas, key);
            drawTextAndIcon(canvas, key, delDrawable);
        } else if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
            if (isCap) {
                drawKeyBackground(drawable, canvas, key);
                drawTextAndIcon(canvas, key, upDrawable);
            } else {
                drawKeyBackground(drawable, canvas, key);
                drawTextAndIcon(canvas, key, lowDrawable);
            }
        } else if (key.codes[0] == KEYCODE_SWITCH) {
            drawKeyBackground(drawable, canvas, key);
            drawTextAndIcon(canvas, key, null);

        } else if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
            drawKeyBackground(drawable, canvas, key);
            drawTextAndIcon(canvas, key, null);
        } else if (key.codes[0] == KEYCODE_CLOSE) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.keyboard_close);
            drawKeyBackground(drawable, canvas, key);
            drawTextAndIcon(canvas, key, null);
        }
    }

    private void drawKeyBackground(Drawable drawable, Canvas canvas, Keyboard.Key key) {

        int[] state = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            drawable.setState(state);
        }
        drawable.setBounds(key.x, key.y + dip2px(HORIZONTAL_GAP), key.x + key.width, key.y + key.height + dip2px(HORIZONTAL_GAP));
        drawable.draw(canvas);
    }

    private void drawTextAndIcon(Canvas canvas, Keyboard.Key key, @Nullable Drawable drawable) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(ContextCompat.getColor(mContext, R.color.color_1A));
            if (key.codes[0] == KEYCODE_CLOSE) {
                paint.setColor(ContextCompat.getColor(mContext, R.color.color_white));
            }

            if (key.label != null) {
                int labelTextSize = 26;
                paint.setTextSize(labelTextSize);
                paint.setTypeface(Typeface.DEFAULT);

                if (key.codes[0] == KEYCODE_SWITCH) {
                    String str = key.label.toString();
                    paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
                    canvas.drawText(str.substring(0, 2), key.x + (key.width / 2) - 10,
                            (key.y + key.height / 2) + bounds.height() / 2 + dip2px(HORIZONTAL_GAP), paint);

                    paint.setTextSize(20);
                    canvas.drawText(str.substring(2, 3), key.x + (key.width / 2) + 18,
                            (key.y + key.height / 2) + bounds.height() / 2 + dip2px(HORIZONTAL_GAP), paint);
                } else {
                    paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
                    canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                            (key.y + key.height / 2) + bounds.height() / 2 + dip2px(HORIZONTAL_GAP), paint);
                }

            }
            if (drawable == null) {
                return;
            }
            // 约定: 最终图标的宽度和高度都需要在按键的宽度和高度的二分之一以内
            // 如果: 图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 那就不需要变换, 否则就需要等比例缩小
            int iconSizeWidth, iconSizeHeight;
            key.icon = drawable;
            int iconH = px2dip(mContext, key.icon.getIntrinsicHeight());
            int iconW = px2dip(mContext, key.icon.getIntrinsicWidth());
            if (key.width >= (ICON2KEY * iconW) && key.height >= (ICON2KEY * iconH)) {
                //图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 不需要缩放, 因为图片已经够小或者按键够大
                setIconSize(canvas, key, iconW, iconH);
            } else {
                //图标的实际宽度和高度至少有一个不在按键的宽度或高度的二分之一以内, 需要等比例缩放, 因为此时图标的宽或者高已经超过按键的二分之一
                //需要把超过的那个值设置为按键的二分之一, 另一个等比例缩放
                //不管图标大小是多少, 都以宽度width为标准, 把图标的宽度缩放到和按键一样大, 并同比例缩放高度
                double multi = 1.0 * iconW / key.width;
                int tempIconH = (int) (iconH / multi);
                if (tempIconH <= key.height) {
                    //宽度相等时, 图标的高度小于等于按键的高度, 按照现在的宽度和高度设置图标的最终宽度和高度
                    iconSizeHeight = tempIconH / ICON2KEY;
                    iconSizeWidth = key.width / ICON2KEY;
                } else {
                    //宽度相等时, 图标的高度大于按键的高度, 这时按键放不下图标, 需要重新按照高度缩放
                    double mul = 1.0 * iconH / key.height;
                    int tempIconW = (int) (iconW / mul);
                    iconSizeHeight = key.height / ICON2KEY;
                    iconSizeWidth = tempIconW / ICON2KEY;
                }
                setIconSize(canvas, key, iconSizeWidth, iconSizeHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIconSize(Canvas canvas, Keyboard.Key key, int iconSizeWidth, int iconSizeHeight) {
        int left = key.x + (key.width - iconSizeWidth) / 2;
        int top = key.y + (key.height - iconSizeHeight) / 2;
        int right = key.x + (key.width + iconSizeWidth) / 2;
        int bottom = key.y + (key.height + iconSizeHeight) / 2;
        key.icon.setBounds(left, top + dip2px(HORIZONTAL_GAP), right, bottom + dip2px(HORIZONTAL_GAP));
        key.icon.draw(canvas);
        key.icon = null;
    }

    public void setCap(boolean cap) {
        isCap = cap;
    }

    public void setCN(boolean cn) {
        isCN = cn;
    }

    public void setDelDrawable(Drawable delDrawable) {
        this.delDrawable = delDrawable;
    }

    public void setLowDrawable(Drawable lowDrawable) {
        this.lowDrawable = lowDrawable;
    }

    public void setUpDrawable(Drawable upDrawable) {
        this.upDrawable = upDrawable;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
