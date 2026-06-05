package com.example.floodguard.model;

public class SafetyTipModel {
    private String tipId;
    private String category; // "go_bag" | "before_flood" | "during_flood" | "after_flood" | "evacuation" | "water_safety"
    private String title;
    private String content;
    private int order;
    private boolean isActive;

    public SafetyTipModel() {}

    public String getTipId() { return tipId; }
    public void setTipId(String tipId) { this.tipId = tipId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
