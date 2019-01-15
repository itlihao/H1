package com.zhiyihealth.registration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.zhiyihealth.registration.lib_base.base.BaseActivity;
import com.zhiyihealth.registration.lib_base.constants.Components;
import com.zhiyihealth.registration.lib_base.utils.ToastUtils;

/**
 * @author Lihao
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStartActivity();
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
//            ToastUtils.showToast(MainActivity.this, "跳转成功");
        } else {
            ToastUtils.showToast(MainActivity.this, "**组件初始化失败");
        }
    };
}
