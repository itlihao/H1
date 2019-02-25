package com.hospital.s1m.lib_user.bean;


import com.hospital.s1m.lib_base.entity.EmnuLeft;

import java.util.ArrayList;

/**
 *
 * @author Lihao
 */

public class MenuItem {
    public static final String QUIT_SYSTEM = "QUIT_SYSTEM";
    public static final String PRINT_CHECK = "PRINT_CHECK";
    public static final String FAST_REGISTER = "FAST_REGISTER";


    private static ArrayList<EmnuLeft> emnu = new ArrayList();

    static {
        emnu.add(new EmnuLeft(FAST_REGISTER, "快速挂号模式", EmnuLeft.ITEM_IMAGE));
        emnu.add(new EmnuLeft(PRINT_CHECK, "自动打印叫号单", EmnuLeft.ITEM_IMAGE));
        emnu.add(new EmnuLeft(QUIT_SYSTEM, "退出账号"));
    }


    public static ArrayList getEmnu() {
        return emnu;
    }

}
