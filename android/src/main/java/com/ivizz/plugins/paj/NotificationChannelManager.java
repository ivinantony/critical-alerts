package com.ivizz.plugins.paj;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings; // This import is not used, can be removed.
import androidx.core.app.NotificationCompat;
import com.getcapacitor.*;
import com.getcapacitor.util.WebColor;
import java.util.Arrays; // This import is not used, can be removed.
import java.util.List; // This import is not used, can be removed.

public class NotificationChannelManager {

    private Context context;
    private NotificationManager notificationManager;
    private PluginConfig config; // This field is not used in the provided methods, can be removed if not used elsewhere.

    // Constructor to initialize the manager with context, NotificationManager, and plugin config.
    public NotificationChannelManager(Context context, NotificationManager manager, PluginConfig config) {
        this.context = context;
        this.notificationManager = manager;
        this.config = config;
    }

    // Static strings for channel property keys.
    private static String CHANNEL_ID = "id";
    private static String CHANNEL_NAME = "name";
    private static String CHANNEL_DESCRIPTION = "description";
    private static String CHANNEL_IMPORTANCE = "importance"; // Not directly used when setting importance for the channel, consider using this to dynamically set importance.
    private static String CHANNEL_VISIBILITY = "visibility";
    private static String CHANNEL_SOUND = "sound";
    private static String CHANNEL_VIBRATE = "vibration";
    private static String CHANNEL_USE_LIGHTS = "lights";
    private static String CHANNEL_LIGHT_COLOR = "lightColor";
    private static String BYPASS_DND = "bypassDnd";

    /**
     * Creates a notification channel based on the properties provided in the PluginCall.
     * This method handles checking for Android O (API 26) or higher.
     * @param call The PluginCall containing the channel details.
     */
    public void createChannel(PluginCall call) {
        // Notification Channels are available from Android O (API 26) onwards.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            JSObject channel = new JSObject();

            // Validate and set channel ID.
            if (call.getString(CHANNEL_ID) != null) {
                channel.put(CHANNEL_ID, call.getString(CHANNEL_ID));
            } else {
                call.reject("Channel missing identifier");
                return;
            }

            // Validate and set channel name.
            if (call.getString(CHANNEL_NAME) != null) {
                channel.put(CHANNEL_NAME, call.getString(CHANNEL_NAME));
            } else {
                call.reject("Channel missing name");
                return;
            }

            // Put other channel properties with default values if not provided.
            // Note: CHANNEL_IMPORTANCE is read from call but then hardcoded to IMPORTANCE_HIGH later.
            channel.put(CHANNEL_IMPORTANCE, call.getInt(CHANNEL_IMPORTANCE, NotificationManager.IMPORTANCE_DEFAULT));
            channel.put(CHANNEL_DESCRIPTION, call.getString(CHANNEL_DESCRIPTION, ""));
            channel.put(CHANNEL_VISIBILITY, call.getInt(CHANNEL_VISIBILITY, NotificationCompat.VISIBILITY_PUBLIC));
            channel.put(CHANNEL_SOUND, call.getString(CHANNEL_SOUND, null));
            channel.put(CHANNEL_VIBRATE, call.getBoolean(CHANNEL_VIBRATE, false));
            channel.put(BYPASS_DND, call.getBoolean(BYPASS_DND, false));
            channel.put(CHANNEL_USE_LIGHTS, call.getBoolean(CHANNEL_USE_LIGHTS, false));
            channel.put(CHANNEL_LIGHT_COLOR, call.getString(CHANNEL_LIGHT_COLOR, null));

            // Call the overloaded method to actually create the channel.
            createChannel(channel);
            call.resolve();
        } else {
            // If API level is below O, channels are not supported.
            call.unavailable();
        }
    }

    /**
     * Creates a notification channel from a JSObject.
     * This method contains the core logic for setting up the NotificationChannel.
     * @param channel The JSObject containing the channel details.
     */
    public void createChannel(JSObject channel) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a new NotificationChannel instance.
            // Currently, importance is hardcoded to IMPORTANCE_HIGH.
            // Consider using `channel.getInteger(CHANNEL_IMPORTANCE)` here for dynamic importance.
            NotificationChannel notificationChannel = new NotificationChannel(
                channel.getString(CHANNEL_ID),
                channel.getString(CHANNEL_NAME),
                NotificationManager.IMPORTANCE_HIGH // Hardcoded importance.
            );

            // Set channel description.
            notificationChannel.setDescription(channel.getString(CHANNEL_DESCRIPTION));
            // Set lockscreen visibility.
            notificationChannel.setLockscreenVisibility(channel.getInteger(CHANNEL_VISIBILITY));
            // Enable or disable vibration.
            notificationChannel.enableVibration(channel.getBool(CHANNEL_VIBRATE));
            // Enable or disable lights.
            notificationChannel.enableLights(channel.getBool(CHANNEL_USE_LIGHTS));
            // Set whether this channel bypasses Do Not Disturb mode.
            notificationChannel.setBypassDnd(channel.getBool(BYPASS_DND));

            // Set light color if provided.
            String lightColor = channel.getString(CHANNEL_LIGHT_COLOR);
            if (lightColor != null) {
                try {
                    notificationChannel.setLightColor(WebColor.parseColor(lightColor));
                } catch (IllegalArgumentException ex) {
                    // Log error if color is invalid.
                    Logger.error(Logger.tags("NotificationChannel"), "Invalid color provided for light color.", null);
                }
            }

            // Handle notification sound.
            String sound = channel.getString(CHANNEL_SOUND, null);
            if (sound != null && !sound.isEmpty()) {
                // Remove file extension if present.
                if (sound.contains(".")) {
                    sound = sound.substring(0, sound.lastIndexOf('.'));
                }

                // Determine AudioAttributes usage based on bypassDnd setting.
                // USAGE_ALARM is intended for sounds that should break through DND.
                // USAGE_NOTIFICATION is typically silenced by DND.
                AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);

                if (channel.getBool(BYPASS_DND)) {
                    // If bypassDnd is true, use USAGE_ALARM to play sound in DND.
                    audioAttributesBuilder.setUsage(AudioAttributes.USAGE_ALARM);
                    Logger.debug(Logger.tags("NotificationChannel"), "Setting USAGE_ALARM for sound to bypass DND.");
                } else {
                    // Otherwise, use USAGE_NOTIFICATION.
                    audioAttributesBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
                    Logger.debug(Logger.tags("NotificationChannel"), "Setting USAGE_NOTIFICATION for sound (respects DND).");
                }
                AudioAttributes audioAttributes = audioAttributesBuilder.build();

                // Build the URI for the raw sound resource.
                Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/" + sound);
                // Set the sound and audio attributes for the channel.
                notificationChannel.setSound(soundUri, audioAttributes);
            }

            // Create or update the notification channel with the NotificationManager.
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // public void deleteChannel(PluginCall call) {
    //     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    //         String channelId = call.getString("id");
    //         notificationManager.deleteNotificationChannel(channelId);
    //         call.resolve();
    //     } else {
    //         call.unavailable();
    //     }
    // }

    // public void listChannels(PluginCall call) {
    //     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    //         List<NotificationChannel> notificationChannels = notificationManager.getNotificationChannels();
    //         JSArray channels = new JSArray();
    //         for (NotificationChannel notificationChannel : notificationChannels) {
    //             JSObject channel = new JSObject();
    //             channel.put(CHANNEL_ID, notificationChannel.getId());
    //             channel.put(CHANNEL_NAME, notificationChannel.getName());
    //             channel.put(CHANNEL_DESCRIPTION, notificationChannel.getDescription());
    //             channel.put(CHANNEL_IMPORTANCE, notificationChannel.getImportance());
    //             channel.put(CHANNEL_VISIBILITY, notificationChannel.getLockscreenVisibility());
    //             channel.put(CHANNEL_SOUND, notificationChannel.getSound());
    //             channel.put(CHANNEL_VIBRATE, notificationChannel.shouldVibrate());
    //             channel.put(CHANNEL_USE_LIGHTS, notificationChannel.shouldShowLights());
    //             channel.put(CHANNEL_LIGHT_COLOR, String.format("#%06X", (0xFFFFFF & notificationChannel.getLightColor())));
    //             Logger.debug(Logger.tags("NotificationChannel"), "visibility " + notificationChannel.getLockscreenVisibility());
    //             Logger.debug(Logger.tags("NotificationChannel"), "importance " + notificationChannel.getImportance());
    //             channels.put(channel);
    //         }
    //         JSObject result = new JSObject();
    //         result.put("channels", channels);
    //         call.resolve(result);
    //     } else {
    //         call.unavailable();
    //     }
    // }
}
