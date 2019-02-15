package registration.zhiyihealth.com.h1m.ui;

import android.content.Intent;
import android.os.Bundle;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.zhiyihealth.registration.lib_base.base.BaseActivity;
import com.zhiyihealth.registration.lib_base.constants.Components;
import com.zhiyihealth.registration.lib_base.utils.LogUtils;
import com.zhiyihealth.registration.lib_base.utils.ToastUtils;

import registration.zhiyihealth.com.h1m.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
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
            LogUtils.w("MainActivity", "跳转成功");
            finish();
        } else {
            ToastUtils.showToast(MainActivity.this, "**组件初始化失败");
        }
    };
}
