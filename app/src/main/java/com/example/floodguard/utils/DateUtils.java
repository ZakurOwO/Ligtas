package com.example.floodguard.utils;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatTimestamp(Timestamp ts) {
        if (ts == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault());
        return sdf.format(ts.toDate());
    }

    public static String timeAgo(Timestamp ts) {
        if (ts == null) return "";
        long time = ts.toDate().getTime();
        long now = System.currentTimeMillis();
        long diff = now - time;

        if (diff < 60000) return "Just now";
        if (diff < 3600000) return (diff / 60000) + " mins ago";
        if (diff < 86400000) return (diff / 3600000) + " hours ago";
        if (diff < 172800000) return "Yesterday";
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return sdf.format(ts.toDate());
    }
}
