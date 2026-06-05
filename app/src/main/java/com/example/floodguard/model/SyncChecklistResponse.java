package com.example.floodguard.model;

import com.google.gson.annotations.SerializedName;

public class SyncChecklistResponse {
    private boolean success;
    private String message;

    @SerializedName("saved_count")
    private int savedCount;

    @SerializedName("synced_at")
    private String syncedAt;

    public SyncChecklistResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getSavedCount() { return savedCount; }
    public void setSavedCount(int savedCount) { this.savedCount = savedCount; }

    public String getSyncedAt() { return syncedAt; }
    public void setSyncedAt(String syncedAt) { this.syncedAt = syncedAt; }
}
