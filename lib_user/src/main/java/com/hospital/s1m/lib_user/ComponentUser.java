package com.hospital.s1m.lib_user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.utils.DeviceInfoUtils;
import com.hospital.s1m.lib_user.fragment.FragmentMenu;
import com.hospital.s1m.lib_user.ui.LoginActivity;
import com.hospital.s1m.lib_user.ui.LoginMActivity;

/**
 * Created by Lihao on 2019-1-9.
 * Email heaolihao@163.com
 */
public class ComponentUser implements IComponent {
    @Override
    public String getName() {
        return Components.COMPONENT_USER;
    }

    @Override
    public boolean onCall(CC cc) {
        final Context context = cc.getContext();
        String actionName = cc.getActionName();
        switch (actionName) {
            case Components.COMPONENT_USER_JUMP:
                todoLogin(cc);
                break;
            case Components.COMPONENT_USER_MENU:
                CC.sendCCResult(cc.getCallId(), CCResult.success("Fragment_Jurisdiction",
                        new FragmentMenu()).addData("int", 3));
                break;
            default:
                break;
        }
        return false;
    }

    private void todoLogin(CC cc) {
        Context context = cc.getContext();
        Intent intent = new Intent(context, LoginMActivity.class);
        if (DeviceInfoUtils.isSunMiT1mini()) {
            intent = new Intent(context, LoginActivity.class);
        } else if (DeviceInfoUtils.isSunMiPos()){
            intent = new Intent(context, LoginMActivity.class);
        }

        if (!(context instanceof Activity)) {
            //调用方没有设置context或app间组件跳转，context为application
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }
}
