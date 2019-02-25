package com.hospital.s1m.lib_base.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumUtils {
	
	public static String SpeciclaNum = "0123456789- ";
	
	/** 
     * 手机号验证 
     *  
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isPhone(String str) {
//        Pattern p = null;
//        Matcher m = null;
//        boolean b = false;
//        p = Pattern.compile("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)"); // 验证手机号  
//        m = p.matcher(str);
//        b = m.matches();
    	for (int i = 0; i < str.length(); i++) {
			char charAt = str.charAt(i);
			if(!SpeciclaNum.contains(""+charAt)){
				return false;
			}
		}
        return true;  
    }  
    /** 
     * 电话号码验证 
     *  
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isPhones(String str) {
        Pattern p1 = null,p2 = null;
        Matcher m = null;
        boolean b = false;    
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if(str.length() >9)  
        {   m = p1.matcher(str);  
            b = m.matches();    
        }else{  
            m = p2.matcher(str);  
            b = m.matches();   
        }    
        return b;  
    }

    /**
     * 验证手机号
     * @param mobiles
     * @return
     */
    public static boolean isPhoneNumber(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    但是由于运营商段位的增加，所以取消第二位的判定
    */
        String telRegex = "[1]\\d{10}";//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);

    }

}
