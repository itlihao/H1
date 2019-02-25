package com.hospital.s1m.lib_base.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PinyinUtils {

    /**
     * 根据传入的字符串(包含汉字),得到拼音 杨亚坤 -> YANGYAKUN 张 涵*& -> ZHANGHAN 流星f5 -> 流星
     *
     * @param str 字符串
     * @return
     */
    public static String getPinyin(String str) {

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();

        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            // 如果是空格, 跳过
            if (Character.isWhitespace(c)) {
                continue;
            }

            if (c >= -127 && c < 128) {
                // 肯定不是汉字
                sb.append("#");
            } else {
                String s = "";
                try {
                    // 通过char得到拼音集合. 单 -> dan, shan
                    s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
                    sb.append(s);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                    sb.append(s);
                }
            }
        }

        return sb.toString();
    }

    /**
     * 将汉字转换成拼音全拼
     */
    public static String getPinYin(String src) {
        char[] t1;
        t1 = src.toCharArray();
        String[] t2;
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String pinyin = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    // 将汉字的几种全拼都存到t2数组中
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    // 取出该汉字全拼的第一种读音并连接到字符串pinyin后
                    pinyin += t2[0];
                } else {
                    // 如果不是汉字字符，直接取出字符并连接到字符串pinyin后
                    pinyin += Character.toString(t1[i]).toLowerCase();
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pinyin;
    }


    /**
     * 获取拼音简拼
     *
     * @param str
     * @return
     */
    public static String getPinyinjp(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();

        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            // 如果是空格, 跳过
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c >= -127 && c < 128) {
                if (c >= 65 && c <= 90) {
                    sb.append(c);
                } else if (c >= 97 && c <= 122) {
                    sb.append(c);
                } else if (c >= 48 && c <= 57) {
                    sb.append(c);
                } else
                    // 肯定不是汉字
                    sb.append("#");
            } else {
                String s = "";
                try {
                    // 通过char得到拼音集合. 单 -> dan, shan
                    if (PinyinHelper.toHanyuPinyinStringArray(c, format) != null && PinyinHelper.toHanyuPinyinStringArray(c, format).length > 0 && PinyinHelper.toHanyuPinyinStringArray(c, format)[0] != null) {
                        s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
                        sb.append(s.charAt(0));
                    } else {
                        sb.append(c);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                    sb.append(s);
                }
            }
        }

        return sb.toString().toUpperCase();
    }


    public static boolean isHan(String string) {

        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isWhitespace(c)) {
                return false;
            }
            if (isChinese(c)) {
                return false;
            } else {
                continue;
            }
        }
        return true;
    }


    public static boolean isHanPrint(String string) {

        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isWhitespace(c)) {
                return false;
            }
            if (c >= -127 && c < 128) {
                if (c >= 65 && c <= 90) {
                    continue;
                } else if (c >= 97 && c <= 122)
                    continue;
                else if (c >= 48 && c <= 57)
                    continue;
                return false;
            }
        }
        return true;
    }


    /**
     * 根据Unicode编码完美的判断中文汉字和符号
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    // 只能判断部分CJK字符（CJK统一汉字）
    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str.trim()).find();
    }


    /**
     * 2~10个汉字数字或字母
     *
     * @param charArray 要判定的数据
     * @return
     */
    public static boolean isPatientName(String charArray) {
        if (TextUtils.isEmpty(charArray)) {
            charArray = "";
        }
        if (charArray.length() < 2 || charArray.length() > 20) {
            return false;
        }
        for (int i = 0; i < charArray.length(); i++) {
            char c = charArray.charAt(i);
            if (c == 34 || c == 39) {
                return false;
            }
//			if(isChineseByREG(c+"")){
//				continue;
//			}else if(Character.isUpperCase(c)||Character.isLowerCase(c)){//大写字母或小写字母
//				continue;
//			} else if(Character.isDigit(c)){//数字
//				continue;
//			} else if (c == 183) {
//				continue;
//			} else {
//				return false;
//			}
        }
        return true;
    }


    /**
     * 提取每个汉字的首字母
     *
     * @param str
     * @return String
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变，特殊字符丢失 支持多音字，生成方式如（长沙市长:cssc,zssz,zssc,cssz）
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String getPinyinjp5(String chines) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    // 取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat);
                    if (nameChar[i] == '余') {
                        strs = new String[]{"yu"};
                    }
                    if (strs != null) {
                        for (int j = 0; j < strs.length; j++) {
                            // 取首字母
                            pinyinName.append(strs[j].charAt(0));
                            if (j != strs.length - 1) {
                                pinyinName.append(",");
                            }
                        }
                    }
                    // else {
                    // pinyinName.append(nameChar[i]);
                    // }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
            pinyinName.append(" ");
        }
        // return pinyinName.toString();
        return parseTheChineseByObject(discountTheChinese(pinyinName.toString())).toUpperCase();

    }

    /**
     * 去除多音字重复数据
     *
     * @param theStr
     * @return
     */
    private static List<Map<String, Integer>> discountTheChinese(String theStr) {
        // 去除重复拼音后的拼音列表
        List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
        // 用于处理每个字的多音字，去掉重复
        Map<String, Integer> onlyOne = null;
        String[] firsts = theStr.split(" ");
        // 读出每个汉字的拼音
        for (String str : firsts) {
            onlyOne = new Hashtable<String, Integer>();
            String[] china = str.split(",");
            // 多音字处理
            for (String s : china) {
                Integer count = onlyOne.get(s);
                if (count == null) {
                    onlyOne.put(s, new Integer(1));
                } else {
                    onlyOne.remove(s);
                    count++;
                    onlyOne.put(s, count);
                }
            }
            mapList.add(onlyOne);
        }
        return mapList;
    }


    /**
     * 解析并组合拼音，对象合并方案(推荐使用)
     *
     * @return
     */
    private static String parseTheChineseByObject(
            List<Map<String, Integer>> list) {
        Map<String, Integer> first = null; // 用于统计每一次,集合组合数据
        // 遍历每一组集合
        synchronized (list) {
            // 遍历每一组集合
            for (int i = 0; i < list.size(); i++) {
                // 每一组集合与上一次组合的Map
                Map<String, Integer> temp = new Hashtable<String, Integer>();
                // 第一次循环，first为空
                if (first != null) {
                    // 取出上次组合与此次集合的字符，并保存
                    for (String s : first.keySet()) {
                        for (String s1 : list.get(i).keySet()) {
                            String str = s + s1;
                            temp.put(str, 1);
                        }
                    }
                    // 清理上一次组合数据
                    if (temp != null && temp.size() > 0) {
                        first.clear();
                    }
                } else {
                    for (String s : list.get(i).keySet()) {
                        String str = s;
                        temp.put(str, 1);
                    }
                }
                // 保存组合数据以便下次循环使用
                if (temp != null && temp.size() > 0) {
                    first = temp;
                }
            }
            String returnStr = "";
            if (first != null) {
                // 遍历取出组合字符串
                for (String str : first.keySet()) {
                    returnStr += (str + ",");
                }
            }
            if (returnStr.length() > 0) {
                returnStr = returnStr.substring(0, returnStr.length() - 1);
            }
            return returnStr;

        }
    }
}
