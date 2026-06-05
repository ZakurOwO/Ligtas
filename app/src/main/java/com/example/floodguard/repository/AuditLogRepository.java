package com.example.floodguard.repository;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AuditLogRepository {
    private static AuditLogRepository instance;
    private final FirebaseFirestore db;

    private AuditLogRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized AuditLogRepository getInstance() {
        if (instance == null) {
            instance = new AuditLogRepository();
        }
        return instance;
    }

    public void log(String module, String action, String status) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> payload = new HashMap<>();
        payload.put("module", module);
        payload.put("action", action);
        payload.put("status", status);
        payload.put("timestamp", Timestamp.now());
        payload.put("uid", user != null ? user.getUid() : "anonymous");
        payload.put("user", user != null && user.getEmail() != null ? user.getEmail() : "Mobile user");
        db.collection("auditLogs").add(payload);
    }
}
