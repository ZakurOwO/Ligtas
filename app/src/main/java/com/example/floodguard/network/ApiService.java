package com.example.floodguard.network;

import com.example.floodguard.model.Alert;
import com.example.floodguard.model.ChecklistSyncRequest;
import com.example.floodguard.model.SyncChecklistResponse;
import com.example.floodguard.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("get_alerts.php")
    Call<List<Alert>> getAlerts(
        @Query("barangay") String barangay,
        @Query("severity") String severity
    );

    @POST("sync_checklist.php")
    Call<SyncChecklistResponse> syncChecklist(@Body ChecklistSyncRequest request);

    @GET("get_user_profile.php")
    Call<User> getUserProfileCall(@Query("uid") String uid);
}
