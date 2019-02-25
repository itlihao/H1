package com.hospital.s1m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.hospital.s1m.lib_base.constants.Components;

/**
 * Created by Lihao on 2019-1-10.
 * Email heaolihao@163.com
 */
public class ComponentApp implements IComponent {

    @Override
    public String getName() {
        return Components.COMPONENT_APP_MAIN;
    }

    @Override
    public boolean onCall(CC cc) {
        final Context context = cc.getContext();
        String actionName = cc.getActionName();
        switch (actionName) {
            case Components.COMPONENT_APP_JUMP:
                toMain(cc);
                break;
            case Components.COMPONENT_APP_JUMPN:
                toNRegistration(cc);
                break;
            default:
                break;
        }
        return false;
    }

    private void toMain(CC cc) {
        String token = cc.getParamItem("token");
        Context context = cc.getContext();
        Intent intent = new Intent(context, RegistrationActivity.class);
        if (!(context instanceof Activity)) {
            //调用方没有设置context或app间组件跳转，context为application
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }

    private void toNRegistration(CC cc) {
        Context context = cc.getContext();
        Intent intent = new Intent(context, NRegistrationActivity.class);
        if (!(context instanceof Activity)) {
            //调用方没有设置context或app间组件跳转，context为application
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }
}
