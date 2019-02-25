package com.hospital.s1m;

import android.annotation.SuppressLint;
import android.util.Log;

import com.billy.cc.core.component.CC;
import com.hospital.s1m.lib_print.utils.AidlUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.hospital.s1m.lib_base.BaseApplication;
import com.hospital.s1m.lib_base.data.NetDataSource;
import com.hospital.s1m.lib_base.utils.LogUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.jpush.android.api.JPushInterface;
import registration.zhiyihealth.com.lib_ime.manager.PinYinManager;

/**
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public class MyApplication extends BaseApplication {
    private PinYinManager manager;
    @Override
    public void onCreate() {
        super.onCreate();

        NetDataSource.init(this, BuildConfig.DEBUG, "MyApplication");
        // bugly升级配置
        Beta.strToastYourAreTheLatestVersion = "";
        Beta.strToastCheckingUpgrade = "";
        Beta.strToastCheckUpgradeError = "";

        //初始化bugly
        Bugly.init(getApplicationContext(), "0368fdf4c3", BuildConfig.DEBUG);

        CC.enableVerboseLog(true);
        CC.enableDebug(true);
        CC.enableRemoteCC(true);


        //初始化极光
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        /*//初始化极光
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        GrowingIO.startWithConfiguration(this, new Configuration()
                .trackAllFragments()
                .setChannel("XXX应用商店"));
*/
        Log.d("application","User模块需要在Application中注册的内容");
        AidlUtil.getInstance().connectPrinterService(this);

        if (manager == null) {
            manager = PinYinManager.getInstance();
        }

        manager.initialize(this);
    }

    @SuppressLint("SimpleDateFormat")
    private void getNetTime() {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            final String format = formatter.format(calendar.getTime());
//            Toast.makeText(MyApplication.this, "当前网络时间为: \n" + format, Toast.LENGTH_SHORT).show();
            LogUtils.w("", "当前网络时间为: \n" + format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
