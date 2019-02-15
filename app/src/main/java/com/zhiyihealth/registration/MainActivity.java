package com.zhiyihealth.registration;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.hospital.s1m.lib_print.utils.AidlUtil;
import com.zhiyihealth.registration.lib_base.base.BaseActivity;
import com.zhiyihealth.registration.lib_base.constants.Components;
import com.zhiyihealth.registration.lib_base.utils.LogUtils;
import com.zhiyihealth.registration.lib_base.utils.ToastUtils;

/**
 * @author Lihao
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        initStartActivity();
//        initView();
    }

    private void initView() {
        Bitmap bitmap = AidlUtil.createImage("https://www.baidu.com/");
//        Bitmap bitmap = AidlUtil.createTime("上午 08:00-12:00", "下午 14:00-18:00", "晚上 19:00-21:00");
//        Bitmap bitmap = AidlUtil.createNum("");
        ImageView iv = findViewById(R.id.iv_test);
        iv.setImageBitmap(bitmap);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    private void initStartActivity() {
        CC.obtainBuilder(Components.COMPONENT_USER)
                .setActionName(Components.COMPONENT_USER_JUMP)
                .build()
                .callAsyncCallbackOnMainThread(mStartCallback);
    }

    IComponentCallback mStartCallback = (cc, result) -> {
        if (result.isSuccess()) {
            LogUtils.w("MainActivity", "跳转成功");
            finish();
        } else {
            ToastUtils.showToast(MainActivity.this, "**组件初始化失败");
        }
    };
}
