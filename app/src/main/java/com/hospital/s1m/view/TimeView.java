package com.hospital.s1m.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hospital.s1m.R;
import com.hospital.s1m.lib_base.utils.DateUtil;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by GuiYanBing on 2018/3/29 13:49
 * E-Mail Address：guiyanbing@zhiyihealth.com.cn
 */
public class TimeView extends LinearLayout {

    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

    private static String split = ":";

    private StringBuilder stringBuilder = new StringBuilder();

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private TextView tvTime;

    /**
     * 初始化完成后执行
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvTime = findViewById(R.id.tv_time);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == View.VISIBLE) {
            timerHandler.sendEmptyMessage(0);
        } else {
            timerHandler.removeMessages(0);
        }
    }

    private void refreshTime() {
        String dateNowStr = DateUtil.format(System.currentTimeMillis(), "yyyy年MM月dd日 EEEE HH:mm:ss");
        tvTime.setText(dateNowStr);
    }

    @SuppressLint("HandlerLeak")
    private Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshTime();
            if (getVisibility() == View.VISIBLE) {
                timerHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };
}
