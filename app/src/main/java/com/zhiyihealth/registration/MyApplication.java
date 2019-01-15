package com.zhiyihealth.registration;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponentCallback;
import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.zhiyihealth.registration.lib_base.BaseApplication;
import com.zhiyihealth.registration.lib_base.constants.Components;
import com.zhiyihealth.registration.lib_base.data.NetDataSource;
import com.zhiyihealth.registration.lib_base.utils.LogUtils;
import com.zhiyihealth.registration.lib_base.utils.ToastUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

/**
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        NetDataSource.init(this, BuildConfig.DEBUG, "MyApplication");
        /*Beta.strToastYourAreTheLatestVersion = "";
        Beta.strToastCheckingUpgrade = "";
        Beta.strToastCheckUpgradeError = "";

        //初始化bugly
        Bugly.init(getApplicationContext(), "382063db84", BuildConfig.DEBUG);
        //初始化极光
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        CC.enableVerboseLog(true);
        CC.enableDebug(true);
        CC.enableRemoteCC(true);

        GrowingIO.startWithConfiguration(this, new Configuration()
                .trackAllFragments()
                .setChannel("XXX应用商店"));
*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                getNetTime();
            }
        }).start();
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
