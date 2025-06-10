package com.ivizz.plugins.paj;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.content.Context;
import com.getcapacitor.JSObject;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "CriticalAlerts")
public class CriticalAlertsPlugin extends Plugin {

    private CriticalAlerts implementation = new CriticalAlerts();
    private NotificationChannelManager notificationChannelManager;
    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void checkDndAccess(PluginCall call) {
        JSObject result = new JSObject();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            boolean granted = manager.isNotificationPolicyAccessGranted();
            result.put("hasAccess", granted);
        } else {
            result.put("hasAccess", true); // DND access not needed pre-Marshmallow
        }
        call.resolve(result);
    }

    @PluginMethod
    public void openDndSettings(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            getActivity().startActivity(intent);
            call.resolve();
        } else {
            call.reject("DND settings not available on this version");
        }
    }

 @PluginMethod
    public void createChannel(PluginCall call) {
        notificationChannelManager.createChannel(call);
    }
//     @PluginMethod
// public void createBypassDndChannel(PluginCall call) {
//     String channelId = call.getString("id");
//     String name = call.getString("name", "Default");
//     String description = call.getString("description", "");
//     String soundName = call.getString("sound", "default");

//     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//         NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

//         // Sound URI
//         Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getContext().getPackageName() + "/raw/" + soundName);

//         AudioAttributes audioAttributes = new AudioAttributes.Builder()
//             .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//             .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//             .build();

//         NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
//         channel.setDescription(description);
//         channel.enableLights(true);
//         channel.enableVibration(true);
//         channel.setSound(soundUri, audioAttributes);
//         channel.setBypassDnd(true); // 🔥 This is the critical line
//         channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

//         notificationManager.createNotificationChannel(channel);
//         call.resolve();
//     } else {
//         call.reject("Notification channels require Android O or higher.");
//     }
// }

}
