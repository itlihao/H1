package registration.zhiyihealth.com.h1m;

import android.util.Log;

import com.billy.cc.core.component.CC;
import com.hospital.s1m.lib_print.utils.AidlUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.zhiyihealth.registration.lib_base.BaseApplication;
import com.zhiyihealth.registration.lib_base.data.NetDataSource;

import registration.zhiyihealth.com.h1m.utils.Density;
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
        //360为UI提供设计图的宽度
        Density.setDensity(this, 720);
        Beta.strToastYourAreTheLatestVersion = "";
        Beta.strToastCheckingUpgrade = "";
        Beta.strToastCheckUpgradeError = "";

        //初始化bugly
        Bugly.init(getApplicationContext(), "1117e55e3e", BuildConfig.DEBUG);

        CC.enableVerboseLog(true);
        CC.enableDebug(true);
        CC.enableRemoteCC(true);


        /*//初始化极光
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        GrowingIO.startWithConfiguration(this, new Configuration()
                .trackAllFragments()
                .setChannel("XXX应用商店"));
*/
        Log.d("application", "User模块需要在Application中注册的内容");
        AidlUtil.getInstance().connectPrinterService(this);

        if (manager == null) {
            manager = PinYinManager.getInstance();
        }

        manager.initialize(this);
    }
}
