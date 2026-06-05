package com.example.floodguard.model;

public class EmergencyContactModel {
    private String contactId;
    private String name;
    private String number;
    private String type; // "mdrrmo" | "barangay_hall" | "pnp" | "bfp" | "ndrrmc" | "other"
    private String iconColor; // "purple", "green", "blue", "orange"
    private int order;
    private boolean isActive;

    public EmergencyContactModel() {}

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIconColor() { return iconColor; }
    public void setIconColor(String iconColor) { this.iconColor = iconColor; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
