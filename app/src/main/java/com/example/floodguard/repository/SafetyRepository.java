package com.example.floodguard.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.data.DefaultMobileData;
import com.example.floodguard.model.GoBagItemModel;
import com.example.floodguard.model.SafetyTipModel;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SafetyRepository {
    private static SafetyRepository instance;
    private final FirebaseFirestore db;

    private SafetyRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized SafetyRepository getInstance() {
        if (instance == null) {
            instance = new SafetyRepository();
        }
        return instance;
    }

    public LiveData<List<SafetyTipModel>> getSafetyTips() {
        MutableLiveData<List<SafetyTipModel>> tipsLiveData = new MutableLiveData<>();
        db.collection("safetyTips")
                .whereEqualTo("isActive", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        tipsLiveData.setValue(DefaultMobileData.safetyTips());
                        return;
                    }
                    if (value != null) {
                        List<SafetyTipModel> tips = new ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            SafetyTipModel tip = doc.toObject(SafetyTipModel.class);
                            tip.setTipId(doc.getId());
                            tips.add(tip);
                        }
                        if (tips.isEmpty()) {
                            tipsLiveData.setValue(DefaultMobileData.safetyTips());
                            return;
                        }
                        Collections.sort(tips, (first, second) -> Integer.compare(first.getOrder(), second.getOrder()));
                        tipsLiveData.setValue(tips);
                    }
                });
        return tipsLiveData;
    }

    public LiveData<List<GoBagItemModel>> getGoBagItems() {
        MutableLiveData<List<GoBagItemModel>> itemsLiveData = new MutableLiveData<>();
        db.collection("goBagItems")
                .orderBy("order", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        itemsLiveData.setValue(DefaultMobileData.goBagItems());
                        return;
                    }
                    if (value != null) {
                        List<GoBagItemModel> items = new ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            GoBagItemModel item = doc.toObject(GoBagItemModel.class);
                            item.setItemId(doc.getId());
                            items.add(item);
                        }
                        if (items.isEmpty()) {
                            itemsLiveData.setValue(DefaultMobileData.goBagItems());
                            return;
                        }
                        itemsLiveData.setValue(items);
                    }
                });
        return itemsLiveData;
    }

    public LiveData<List<String>> getGoBagProgress(String uid) {
        MutableLiveData<List<String>> progressLiveData = new MutableLiveData<>();
        db.collection("goBagProgress").document(uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null && value.exists()) {
                        List<String> checkedItems = (List<String>) value.get("checkedItems");
                        progressLiveData.setValue(checkedItems != null ? checkedItems : new ArrayList<>());
                    } else {
                        progressLiveData.setValue(new ArrayList<>());
                    }
                });
        return progressLiveData;
    }

    public void updateGoBagProgress(String uid, String itemId, boolean isChecked) {
        if (uid == null || uid.isEmpty() || itemId == null || itemId.isEmpty()) return;
        if (isChecked) {
            db.collection("goBagProgress").document(uid)
                    .update("checkedItems", FieldValue.arrayUnion(itemId),
                            "lastUpdated", FieldValue.serverTimestamp())
                    .addOnFailureListener(e -> {
                        // If document doesn't exist, create it
                        List<String> items = new ArrayList<>();
                        items.add(itemId);
                        db.collection("goBagProgress").document(uid)
                                .set(Map.of("checkedItems", items, "lastUpdated", FieldValue.serverTimestamp(), "uid", uid));
                    });
        } else {
            db.collection("goBagProgress").document(uid)
                    .update("checkedItems", FieldValue.arrayRemove(itemId),
                            "lastUpdated", FieldValue.serverTimestamp());
        }
        AuditLogRepository.getInstance().log(
                "Safety Checklist",
                (isChecked ? "Checked " : "Unchecked ") + itemId,
                "Success"
        );
    }
}
