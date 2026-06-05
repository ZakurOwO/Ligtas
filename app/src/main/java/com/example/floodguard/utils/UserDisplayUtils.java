package com.example.floodguard.utils;

import com.example.floodguard.model.UserModel;
import com.google.firebase.auth.FirebaseUser;

public final class UserDisplayUtils {
    private UserDisplayUtils() {}

    public static String initial(UserModel user, FirebaseUser firebaseUser) {
        String name = user != null ? user.getFullName() : null;
        if (name == null || name.trim().isEmpty()) {
            name = firebaseUser != null ? firebaseUser.getDisplayName() : null;
        }
        if (name == null || name.trim().isEmpty()) {
            name = firebaseUser != null ? firebaseUser.getEmail() : null;
        }
        if (name == null || name.trim().isEmpty()) return "U";
        return name.trim().substring(0, 1).toUpperCase();
    }
}
