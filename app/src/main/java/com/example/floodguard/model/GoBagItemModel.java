package com.example.floodguard.model;

public class GoBagItemModel {
    private String itemId;
    private String name;
    private String category;
    private boolean isEssential;
    private boolean isChecked; // Added for state tracking
    private int order;

    public GoBagItemModel() {}

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isEssential() { return isEssential; }
    public void setEssential(boolean essential) { isEssential = essential; }

    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}
