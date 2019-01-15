package com.zhiyihealth.registration.lib_base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

/**
 *
 * @author Lihao
 * @date 2019-1-9
 * Email heaolihao@163.com
 */
public abstract class BaseApplication extends Application {
    public static Stack<Activity> activityStack;

//    private List<BaseAppLogic> logicClassList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initDevicesData();
//        initLogic();
        activityStack = new Stack<>();
        registerActivityLifecycleCallbacks(this);
    }

    private static void registerActivityLifecycleCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityStack.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityStack.remove(activity);
            }
        });
    }

    private void initDevicesData() {
        String type = "2";
        /*CacheDataSource.setImei(StateUtils.getIMEI(this));
        CacheDataSource.setType(type);
        CacheDataSource.setV(StateUtils.packageName(this));*/
    }

//    protected abstract void initLogic();

    /*protected void registerApplicationLogic(BaseAppLogic logicClass) {
        logicClassList.add(logicClass);
        logicClass.setApplication(this);
        logicClass.onCreate();
    }*/


    @Override
    public void onTerminate() {
        super.onTerminate();
        /*for (BaseAppLogic baseAppLogic : logicClassList) {
            baseAppLogic.onTerinate();
        }*/
    }

    /**
     * 获取当前的Activity
     */
    public static Activity getCurActivity() {
        return activityStack.lastElement();
    }
}
