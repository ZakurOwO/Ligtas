package com.example.floodguard.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Min 8 chars, at least 1 number
        return !TextUtils.isEmpty(password) && password.length() >= 8 && password.matches(".*\\d.*");
    }

    public static boolean isNotEmpty(String... fields) {
        for (String field : fields) {
            if (TextUtils.isEmpty(field) || field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
