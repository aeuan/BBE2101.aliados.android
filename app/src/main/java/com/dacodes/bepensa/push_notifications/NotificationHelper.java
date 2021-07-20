package com.dacodes.bepensa.push_notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dacodes.bepensa.R;


public class NotificationHelper extends ContextWrapper {
    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "channeltest";
    public static final String CHANNEL_ONE_NAME = "channeltest";
    public static final String CHANNEL_TWO_ID = "channeltest2";
    public static final String CHANNEL_TWO_NAME = "channeltest2";

//Create your notification channels//

    public NotificationManager notificationManager;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base, NotificationManager notificationManager) {
        super(base);
        this.notificationManager = notificationManager;
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_TWO_ID,
                CHANNEL_TWO_NAME, notifManager.IMPORTANCE_DEFAULT);
        notificationChannel2.enableLights(false);
        notificationChannel2.enableVibration(true);
        notificationChannel2.setLightColor(Color.RED);
        notificationChannel2.setShowBadge(false);
        notificationManager.createNotificationChannel(notificationChannel2);

    }

//Create the notification that’ll be posted to Channel One//

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification1(String title, String body) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
    }

//Create the notification that’ll be posted to Channel Two//

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification2(String title, String body) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_TWO_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notify(int id, Notification.Builder notification) {
        notificationManager.notify(id, notification.build());
    }

//Send your notifications to the NotificationManager system service//

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationManager getManager() {
        if (notifManager != null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}