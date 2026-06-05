package com.example.floodguard.model;

import com.google.gson.annotations.SerializedName;

public class ChecklistItem {
    private Integer id;
    private String name;
    private String category;
    
    @SerializedName("is_essential")
    private boolean isEssential;
    
    @SerializedName("is_checked")
    private boolean isChecked;

    public ChecklistItem() {}

    public ChecklistItem(Integer id, String name, String category, boolean isEssential, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.isEssential = isEssential;
        this.isChecked = isChecked;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isEssential() { return isEssential; }
    public void setEssential(boolean essential) { isEssential = essential; }

    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}
