package com.hospital.s1m.lib_print;

import android.os.Handler;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.utils.DeviceInfoUtils;
import com.hospital.s1m.lib_print.printUsb.PrintRegistration;
import com.hospital.s1m.lib_print.utils.AidlUtil;

/**
 * @author Lihao
 */
public class ComponentPrint implements IComponent {
    @Override
    public String getName() {
        return Components.ComponentPrint;
    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        switch (actionName) {
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
                Handler mainThreadHandler = cc.getParamItem("mainThreadHandler");

                try {
                    if (DeviceInfoUtils.isSunMiT1mini()) {
                        AidlUtil.getInstance().printForm58(cc.getContext(), number, doctorName, registrationId,
                                sysUserId, periodType, registerType, timea, timeh, timey, wait);
                        CC.sendCCResult(cc.getCallId(), CCResult.success());
                    } else if (DeviceInfoUtils.isSunMiD1()) {
                        mainThreadHandler.post(() -> PrintRegistration.getInstance().initPrint(cc));
                    } else {
                        AidlUtil.getInstance().printForm80(cc.getContext(), number, doctorName, registrationId,
                                periodType, registerType, timea, timeh, timey, wait);
                        CC.sendCCResult(cc.getCallId(), CCResult.success());
                    }

                } catch (Exception e) {
                    CC.sendCCResult(cc.getCallId(), CCResult.error("打印失败"));
                    e.printStackTrace();
                }
                break;
            case "0":
                break;
            default:
                break;
        }
        return false;
    }
}
