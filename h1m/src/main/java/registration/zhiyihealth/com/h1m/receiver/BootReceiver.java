package registration.zhiyihealth.com.h1m.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import registration.zhiyihealth.com.h1m.ui.MainActivity;


/**
 * @author Lihao
 */
public class BootReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            /* 服务开机自启动 */
            /*Intent service = new Intent(context, MyService.class);
            context.startService(service);*/
            Intent intent1 = new Intent(context, MainActivity.class);

            intent1.setAction("android.intent.action.MAIN");
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }


    }
}
