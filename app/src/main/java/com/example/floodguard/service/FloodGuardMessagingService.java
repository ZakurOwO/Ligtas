package com.example.floodguard.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.floodguard.MainActivity;
import com.example.floodguard.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

// =============================================================
//  FloodGuardMessagingService.java
//  Receives FCM messages sent by PHP's insert_alert.php and
//  displays a colour-coded system notification.
//
//  Register in AndroidManifest.xml inside <application>:
//
//  <service
//      android:name=".service.FloodGuardMessagingService"
//      android:exported="false">
//      <intent-filter>
//          <action android:name="com.google.firebase.MESSAGING_EVENT"/>
//      </intent-filter>
//  </service>
// =============================================================
public class FloodGuardMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID   = "flood_alerts_channel";
    private static final String CHANNEL_NAME = "Flood Alerts";

    // ── Incoming FCM message ──────────────────────────────────
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        // Prefer notification payload, fall back to data payload
        String title = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getTitle()
                : data.getOrDefault("title", "LigTAS Alert");

        String body = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getBody()
                : data.getOrDefault("description", "New flood alert issued.");

        String severity = data.getOrDefault("severity", "info");

        showNotification(title, body, severity);
    }

    // ── Token refresh ─────────────────────────────────────────
    // Called when FCM rotates the token. Save the new one to Firestore.
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // TODO: update token in Firestore
        // FirebaseFirestore.getInstance()
        //     .collection("users")
        //     .document(FirebaseAuth.getInstance().getUid())
        //     .update("fcm_token", token);
    }

    // ── Build + show the system notification ─────────────────
    private void showNotification(String title, String body, String severity) {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel (required API 26+)
        int importance = ("critical".equals(severity) || "evacuation".equals(severity))
                ? NotificationManager.IMPORTANCE_HIGH
                : NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, importance);
        channel.setDescription("Flood alerts from LigTAS");
        channel.enableVibration(true);
        channel.enableLights(true);
        manager.createNotificationChannel(channel);

        // Tap opens MainActivity on the Alerts tab
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("open_tab", "alerts");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Colour by severity
        int color;
        switch (severity) {
            case "critical":   color = getColor(R.color.severity_critical);   break;
            case "evacuation": color = getColor(R.color.severity_evacuation); break;
            case "warning":    color = getColor(R.color.severity_warning);    break;
            default:           color = getColor(R.color.brand_blue);          break;
        }

        int priority = ("critical".equals(severity) || "evacuation".equals(severity))
                ? NotificationCompat.PRIORITY_HIGH
                : NotificationCompat.PRIORITY_DEFAULT;

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_bell)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setColor(color)
                        .setPriority(priority)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        // Use current time as unique notification ID so multiple alerts stack
        manager.notify((int) System.currentTimeMillis(), notification.build());
    }
}
