package com.example.floodguard;

import android.app.Application;
import com.example.floodguard.utils.NotificationChannelHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FloodGuardApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Enable Firestore offline persistence
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        
        // Create notification channels
        NotificationChannelHelper.createChannels(this);
    }
}
