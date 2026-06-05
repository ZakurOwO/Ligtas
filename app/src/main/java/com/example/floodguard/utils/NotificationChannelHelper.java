package com.example.floodguard.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannelHelper {
    public static final String CHANNEL_ALERTS = "flood_alerts_channel";
    public static final String CHANNEL_REPORTS = "report_updates_channel";
    public static final String CHANNEL_GENERAL = "general_channel";

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager == null) return;

            // Flood Alerts Channel
            NotificationChannel alertsChannel = new NotificationChannel(
                    CHANNEL_ALERTS,
                    "Flood Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            alertsChannel.setDescription("Important notifications regarding flood warnings and evacuations.");

            // Report Updates Channel
            NotificationChannel reportsChannel = new NotificationChannel(
                    CHANNEL_REPORTS,
                    "Report Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            reportsChannel.setDescription("Updates about your submitted flood reports.");

            // General Channel
            NotificationChannel generalChannel = new NotificationChannel(
                    CHANNEL_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_LOW
            );
            generalChannel.setDescription("General announcements and safety tips.");

            manager.createNotificationChannel(alertsChannel);
            manager.createNotificationChannel(reportsChannel);
            manager.createNotificationChannel(generalChannel);
        }
    }
}
