package com.example.floodguard.network;

import com.example.floodguard.model.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("forecast")
    Call<WeatherResponse> getFullWeather(
            @Query("latitude") double lat,
            @Query("longitude") double lng,
            @Query("current") String currentFields,
            @Query("daily") String dailyFields,
            @Query("timezone") String timezone,
            @Query("past_days") int pastDays
    );
}
