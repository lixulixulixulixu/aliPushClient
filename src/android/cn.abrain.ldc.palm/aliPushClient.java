package cn.abrain.ldc.palm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class aliPushClient extends CordovaPlugin {

    public static final String TAG = "aliPushClient";
    private JSONObject params;
    private static CallbackContext pushContext;

    public static CallbackContext getCurrentCallbackContext() {
        return pushContext;
    }

    public  void initCloudChannel(Context applicationContext) {
        this.createNotificationChannel(applicationContext);
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String xiaomi = applicationInfo.metaData.getString("com.alipush.miid");
        String xiaomikey = applicationInfo.metaData.getString("com.alipush.mikey");
        MiPushRegister.register(applicationContext, xiaomi, xiaomikey);
        HuaWeiRegister.register(applicationContext);
    }

    private void registerNotifyCallback(CallbackContext callbackContext) {
        if(PushServiceFactory.getCloudPushService()==null) {
            callbackContext.error("SDK环境初始化失败");
        }else
        {

            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        }
    }
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.initCloudChannel(cordova.getActivity().getApplication());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject arg_object = args.getJSONObject(0);
        if("init".equals(action)){

            String account = "";
            if (arg_object.has("account")) {
                account = arg_object.getString("account");
            }
            Log.i(TAG, "call init bindaccount:"+account);

            final CloudPushService pushService = PushServiceFactory.getCloudPushService();

            final String deviceId = pushService.getDeviceId();
            pushService.bindAccount(account, new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    Log.i(TAG, "bind account success,deviceId:"+deviceId);
                    callbackContext.success(deviceId);
                }

                @Override
                public void onFailed(String s, String s1) {
                    callbackContext.error(s);
                }
            });
        }else if("unbind".equals(action)){
            final CloudPushService pushService = PushServiceFactory.getCloudPushService();
            pushService.unbindAccount(new CommonCallback(){
                @Override
                public void onSuccess(String s) {
                    callbackContext.success();
                }
                @Override
                public void onFailed(String s, String s1) {
                    callbackContext.error(s);
                }
            });
        }
        else if("getMessage".equals(action)) {
            SharedPreferences preferences= getApplicationContext().getSharedPreferences("mynotifyMsg", Context.MODE_PRIVATE);
            String name=preferences.getString("msg", "");

            //设空
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("msg", "");
            editor.commit();

            callbackContext.success(name);
        }
        else if("finish".equals(action)) {
            callbackContext.success();
        } else if ("initstate".equals(action)) {
            CloudPushService pushS = PushServiceFactory.getCloudPushService();
            if(pushS==null) {
                callbackContext.error("SDK环境初始化失败");
            }else
            {
                callbackContext.success("SDK初始化成功");
            }
        }
        else if("registerNotify".equals(action)){
            pushContext = callbackContext;
            registerNotifyCallback(callbackContext);
        }
        else{
            Log.e(TAG, "Invalid action : " + action);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            return false;
        }

        return true;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }

    private void createNotificationChannel(Context applicationContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "abr";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

}
