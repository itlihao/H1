package com.hospital.s1m.lib_base.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

public class MineDialog extends AlertDialog {
    public MineDialog(@NonNull Context context) {
        super(context);
    }

    public MineDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public MineDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        // Dialog 在初始化时会生成新的 Window，先禁止 Dialog Window 获取焦点，等 Dialog 显示后对 Dialog Window 的 DecorView 设置 setSystemUiVisibility ，接着再获取焦点。 这样表面上看起来就没有退出沉浸模式。
        // Set the dialog to not focusable (makes navigation ignore us adding the window)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Show the dialog!
        super.show();

        //Set the dialog to immersive
//        SystemUIUtils.setStickFullScreen(getWindow().getDecorView());

        //Clear the not focusable flag from the window
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}

