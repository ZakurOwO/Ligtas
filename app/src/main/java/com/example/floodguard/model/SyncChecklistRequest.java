package com.example.floodguard.model;

import java.util.List;

public class SyncChecklistRequest {
    private String user_id;
    private List<ChecklistItem> items;

    public SyncChecklistRequest() {}

    public SyncChecklistRequest(String userId, List<ChecklistItem> items) {
        this.user_id = userId;
        this.items = items;
    }

    public String getUserId() { return user_id; }
    public void setUserId(String userId) { this.user_id = userId; }

    public List<ChecklistItem> getItems() { return items; }
    public void setItems(List<ChecklistItem> items) { this.items = items; }
}
