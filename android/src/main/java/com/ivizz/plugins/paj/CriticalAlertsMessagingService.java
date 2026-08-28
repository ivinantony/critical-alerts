package com.ivizz.plugins.paj;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class CriticalAlertsMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData());
        }
    }

    private void showNotification(Map<String, String> data) {
        Context context = getApplicationContext();

        String title = data.get("title");
        String body = data.get("body");
        String sound = data.get("sound");
        String channelId = data.get("android_channel_id");

        if (channelId == null || channelId.isEmpty()) {
            channelId = "default_channel";
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(
                        channelId,
                        "Custom Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.enableVibration(true);
                channel.enableLights(true);

                if (sound != null && !sound.isEmpty()) {
                    String soundName = sound.contains(".") ? sound.substring(0, sound.lastIndexOf('.')) : sound;
                    Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                            context.getPackageName() + "/raw/" + soundName);
                    channel.setSound(soundUri, buildAudioAttributes(notificationManager));
                }

                notificationManager.createNotificationChannel(channel);
            }
        }

        int iconRes = context.getResources().getIdentifier("ic_notification", "drawable", context.getPackageName());
        if (iconRes == 0) {
            iconRes = context.getApplicationInfo().icon;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title != null ? title : "Notification")
                .setContentText(body != null ? body : "")
                .setSmallIcon(iconRes)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (sound != null && !sound.isEmpty() && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            String soundName = sound.contains(".") ? sound.substring(0, sound.lastIndexOf('.')) : sound;
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.getPackageName() + "/raw/" + soundName);
            builder.setSound(soundUri);
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private AudioAttributes buildAudioAttributes(NotificationManager nm) {
        boolean hasDndAccess = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasDndAccess = nm.isNotificationPolicyAccessGranted();
        }
        return new AudioAttributes.Builder()
                .setUsage(hasDndAccess ? AudioAttributes.USAGE_ALARM : AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
    }
}
