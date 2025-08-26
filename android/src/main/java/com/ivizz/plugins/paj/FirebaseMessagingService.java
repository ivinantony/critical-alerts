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

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Only handle data payload
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            showNotification(data);
        }
    }

    private void showNotification(Map<String, String> data) {
        Context context = getApplicationContext();

        String title = data.get("title");
        String body = data.get("body");
        String sound = data.get("sound"); // optional
        String channelId = data.get("android_channel_id");

        if (channelId == null || channelId.isEmpty()) {
            channelId = "default_channel"; // fallback
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel if not exists (Oreo+)
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

                if (sound != null) {
                    Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                            context.getPackageName() + "/raw/" + sound);

                    channel.setSound(soundUri, getAudioAttributes(notificationManager));
                }

                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title != null ? title : "Notification")
                .setContentText(body != null ? body : "")
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Set sound for pre-Oreo devices
        if (sound != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.getPackageName() + "/raw/" + sound);
            builder.setSound(soundUri);
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private AudioAttributes getAudioAttributes(NotificationManager nm) {
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
