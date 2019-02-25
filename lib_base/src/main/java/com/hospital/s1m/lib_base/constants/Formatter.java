package com.hospital.s1m.lib_base.constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by wyl on 2017/4/24.
 * Formatter
 */

public interface Formatter {
    SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT4 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT5 = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT6 = new SimpleDateFormat("MMddHHmmss", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT7 = new SimpleDateFormat("HH:mm", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT8 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT9 = new SimpleDateFormat("mm:ss", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT10 = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT11 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT12 = new SimpleDateFormat("yyyy-MM-dd  ahh", Locale.CHINA);
    SimpleDateFormat DATE_FORMAT0 = new SimpleDateFormat("yyyy年MM月dd日 EEEE HH:mm:ss", Locale.CHINA);
}
