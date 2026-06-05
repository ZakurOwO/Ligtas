package com.example.floodguard.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// =============================================================
//  AlertsResponse.java
//  Maps the full JSON envelope from get_alerts.php:
//  { "success": true, "count": 3, "data": [ ... ] }
// =============================================================
public class AlertsResponse {

    private boolean success;
    private int count;
    private int limit;
    private int offset;

    @SerializedName("data")
    private List<Alert> data;

    public AlertsResponse() {}

    public boolean isSuccess()      { return success; }
    public int getCount()           { return count; }
    public int getLimit()           { return limit; }
    public int getOffset()          { return offset; }
    public List<Alert> getData()    { return data; }

    public void setSuccess(boolean s)       { this.success = s; }
    public void setCount(int count)         { this.count = count; }
    public void setLimit(int limit)         { this.limit = limit; }
    public void setOffset(int offset)       { this.offset = offset; }
    public void setData(List<Alert> data)   { this.data = data; }
}
