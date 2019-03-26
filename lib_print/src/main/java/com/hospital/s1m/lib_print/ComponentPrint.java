package com.hospital.s1m.lib_print;

import android.graphics.Bitmap;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.hospital.s1m.lib_print.utils.AidlUtil;
import com.hospital.s1m.lib_print.utils.QRCodeUtil;
import com.hospital.s1m.lib_base.constants.Components;

public class ComponentPrint implements IComponent {
    @Override
    public String getName() {
        return Components.ComponentPrint;
    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        switch (actionName) {
            /*case Components.ComponentPrintInit:
                CC.sendCCResult(cc.getCallId(), CCResult.success("getApplogic", new ApplogicUser()));
                break;*/
            case Components.ComponentPrintNumber:
                int number = cc.getParamItem("number");
                String doctorName = cc.getParamItem("doctorName");
                String registrationId = cc.getParamItem("registrationId");
                String sysUserId = cc.getParamItem("sysUserId");
                int registerType = cc.getParamItem("registerType");
                int periodType = cc.getParamItem("periodType");
                String timea = cc.getParamItem("timeS1");
                String timeh = cc.getParamItem("timeS2");
                String timey = cc.getParamItem("timeS3");
                String wait = cc.getParamItem("wait");

                try {
                    //居中
                    AidlUtil.getInstance().printNumber(cc.getContext(), number, doctorName, registrationId,
                            sysUserId, periodType, registerType, timea, timeh, timey, wait);
                    CC.sendCCResult(cc.getCallId(), CCResult.success());
                } catch (Exception e) {
                    CC.sendCCResult(cc.getCallId(), CCResult.error("打印失败"));
                    e.printStackTrace();
                }
                break;
            case Components.ComponentPrintQr:
                String data = cc.getParamItem("data");
                String clinicName = cc.getParamItem("clinicName");
                String doctorName1 = cc.getParamItem("doctorName");
                try {
                AidlUtil.getInstance().printQr(data, clinicName, doctorName1);
                CC.sendCCResult(cc.getCallId(), CCResult.success());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    CC.sendCCResult(cc.getCallId(), CCResult.error("打印失败"));
                    e.printStackTrace();
                }
                break;
            case Components.ComponentGetQr:
                String content = cc.getParamItem("content");
                int widthPix = cc.getParamItem("widthPix");
                int heightPix = cc.getParamItem("heightPix");
                Bitmap logoBm = cc.getParamItem("logoBm");
                Bitmap qrImage = QRCodeUtil.createQRImage(content, widthPix, heightPix, logoBm);
                CC.sendCCResult(cc.getCallId(), CCResult.success("code", qrImage));
                break;
            default:
        }
        return true;
    }
}
