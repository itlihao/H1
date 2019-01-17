/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.pinyin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import registration.zhiyihealth.com.lib_ime.R;
import registration.zhiyihealth.com.lib_ime.manager.PinYinManager;

/**
 * View used to show composing string (The Pinyin string for the unselected
 * syllables and the Chinese string for the selected syllables.)
 * @author Lihao
 */
public class NewComposingView extends View {
    public enum ComposingStatus {
        SHOW_PINYIN, SHOW_STRING_LOWERCASE, EDIT_PINYIN,
    }

    private static final int LEFT_RIGHT_MARGIN = 5;

    /**
     * Used to draw composing string. When drawing the active and idle part of
     * the spelling(Pinyin) string, the color may be changed.
     */
    private Paint mPaint;

    /**
     * Drawable used to draw highlight effect.
     */
    private Drawable mHlDrawable;

    /**
     * Drawable used to draw cursor for editing mode.
     */
    private Drawable mCursor;

    /**
     * Used to estimate dimensions to show the string .
     */
    private FontMetricsInt mFmi;

    private int mStrColor;
    private int mStrColorHl;
    private int mStrColorIdle;

    private int mFontSize;

    private ComposingStatus mComposingStatus;

    PinYinManager.DecodingInfo mDecInfo;

    public NewComposingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources r = context.getResources();
        mHlDrawable = ContextCompat.getDrawable(context, R.drawable.composing_hl_bg);
        mCursor = ContextCompat.getDrawable(context, R.drawable.composing_area_cursor);

        mStrColor = ContextCompat.getColor(context, R.color.composing_color);
        mStrColorHl = ContextCompat.getColor(context, R.color.composing_color_hl);
        mStrColorIdle = ContextCompat.getColor(context, R.color.composing_color_idle);

        mFontSize = r.getDimensionPixelSize(R.dimen.composing_height);

        mPaint = new Paint();
        mPaint.setColor(mStrColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mFontSize);

        mFmi = mPaint.getFontMetricsInt();
    }

    public void reset() {
        mComposingStatus = ComposingStatus.SHOW_PINYIN;
    }

    /**
     * Set the composing string to show. If the IME status is
     * be set to {@link ComposingStatus#SHOW_PINYIN}, otherwise the composing
     * view will set its status to {@link ComposingStatus#SHOW_STRING_LOWERCASE}
     * or {@link ComposingStatus#EDIT_PINYIN} automatically.
     */
    public void setDecodingInfo(PinYinManager.DecodingInfo decInfo,
                                PinYinManager.ImeState imeStatus) {
        mDecInfo = decInfo;

        if (PinYinManager.ImeState.STATE_INPUT == imeStatus) {
            mComposingStatus = ComposingStatus.SHOW_PINYIN;
            mDecInfo.moveCursorToEdge(false);
        } else {
            if (decInfo.getFixedLen() != 0
                    || ComposingStatus.EDIT_PINYIN == mComposingStatus) {
                mComposingStatus = ComposingStatus.EDIT_PINYIN;
            } else {
                mComposingStatus = ComposingStatus.SHOW_STRING_LOWERCASE;
            }
            mDecInfo.moveCursor(0);
        }

        measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        requestLayout();
        invalidate();
    }

    public boolean moveCursor(int keyCode) {
        if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT
                && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) {
            return false;
        }

        if (ComposingStatus.EDIT_PINYIN == mComposingStatus) {
            int offset = 0;
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                offset = -1;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                offset = 1;
            }
            mDecInfo.moveCursor(offset);
        } else if (ComposingStatus.SHOW_STRING_LOWERCASE == mComposingStatus) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mComposingStatus = ComposingStatus.EDIT_PINYIN;

                measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                requestLayout();
            }

        }
        invalidate();
        return true;
    }

    public ComposingStatus getComposingStatus() {
        return mComposingStatus;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width;
        int height;
        height = mFmi.bottom - mFmi.top + getPaddingLeft() + getPaddingBottom();

        if (null == mDecInfo) {
            width = 0;
        } else {
            width = getPaddingLeft() + getPaddingRight() + LEFT_RIGHT_MARGIN * 2;

            String str;
            if (ComposingStatus.SHOW_STRING_LOWERCASE == mComposingStatus) {
                str = mDecInfo.getOrigianlSplStr().toString();
            } else {
                str = mDecInfo.getComposingStrForDisplay();
            }
            width += mPaint.measureText(str, 0, str.length());
        }
        setMeasuredDimension((int) (width + 0.5f), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (ComposingStatus.EDIT_PINYIN == mComposingStatus
                || ComposingStatus.SHOW_PINYIN == mComposingStatus) {
            drawForPinyin(canvas);
            return;
        }

        float x, y;
        x = getPaddingLeft() + LEFT_RIGHT_MARGIN;
        y = -mFmi.top + getPaddingTop();

        mPaint.setColor(mStrColorHl);
        mHlDrawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth()
                - getPaddingRight(), getHeight() - getPaddingBottom());
        mHlDrawable.draw(canvas);

        String splStr = mDecInfo.getOrigianlSplStr().toString();
        canvas.drawText(splStr, 0, splStr.length(), x, y, mPaint);
    }

    private void drawCursor(Canvas canvas, float x) {
        mCursor.setBounds((int) x, getPaddingTop(), (int) x
                + mCursor.getIntrinsicWidth(), getHeight() - getPaddingBottom());
        mCursor.draw(canvas);
    }

    private void drawForPinyin(Canvas canvas) {
        float x, y;
        x = getPaddingLeft() + LEFT_RIGHT_MARGIN;
        y = -mFmi.top + getPaddingTop();

        mPaint.setColor(mStrColor);

        int cursorPos = mDecInfo.getCursorPosInCmpsDisplay();
        int cmpsPos = cursorPos;
        String cmpsStr = mDecInfo.getComposingStrForDisplay();
        int activeCmpsLen = mDecInfo.getActiveCmpsDisplayLen();
        if (cursorPos > activeCmpsLen) {
            cmpsPos = activeCmpsLen;
        }
        canvas.drawText(cmpsStr, 0, cmpsPos, x, y, mPaint);
        x += mPaint.measureText(cmpsStr, 0, cmpsPos);
        if (cursorPos <= activeCmpsLen) {
            if (ComposingStatus.EDIT_PINYIN == mComposingStatus) {
                drawCursor(canvas, x);
            }
            canvas.drawText(cmpsStr, cmpsPos, activeCmpsLen, x, y, mPaint);
        }

        x += mPaint.measureText(cmpsStr, cmpsPos, activeCmpsLen);

        if (cmpsStr.length() > activeCmpsLen) {
            mPaint.setColor(mStrColorIdle);
            int oriPos = activeCmpsLen;
            if (cursorPos > activeCmpsLen) {
                if (cursorPos > cmpsStr.length()) {
                    cursorPos = cmpsStr.length();
                }
                canvas.drawText(cmpsStr, oriPos, cursorPos, x, y, mPaint);
                x += mPaint.measureText(cmpsStr, oriPos, cursorPos);

                if (ComposingStatus.EDIT_PINYIN == mComposingStatus) {
                    drawCursor(canvas, x);
                }

                oriPos = cursorPos;
            }
            canvas.drawText(cmpsStr, oriPos, cmpsStr.length(), x, y, mPaint);
        }
    }
}
