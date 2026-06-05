package com.example.floodguard.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.model.WeatherResponse;
import com.example.floodguard.network.WeatherApiService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {
    private static WeatherRepository instance;
    private final WeatherApiService apiService;
    private final FirebaseFirestore db;
    private final MutableLiveData<WeatherResponse> weatherData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    private static final String WEATHER_BASE_URL = "https://api.open-meteo.com/v1/";

    public WeatherRepository() {
        db = FirebaseFirestore.getInstance();
        apiService = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService.class);
    }

    public static synchronized WeatherRepository getInstance() {
        if (instance == null) instance = new WeatherRepository();
        return instance;
    }

    public LiveData<WeatherResponse> getWeather() { return weatherData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    public void fetchWeather(double lat, double lng) {
        String currentFields = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,wind_speed_10m,weather_code";
        String dailyFields = "weather_code,temperature_2m_max,temperature_2m_min";

        apiService.getFullWeather(lat, lng, currentFields, dailyFields, "auto", 0)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getCurrent() != null) {
                            weatherData.postValue(response.body());
                        } else {
                            loadFirebaseWeather("Weather service returned " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        loadFirebaseWeather("Using cached weather data.");
                    }
                });
    }

    private void loadFirebaseWeather(String fallbackMessage) {
        db.collection("weatherCache").document("current").get()
                .addOnSuccessListener(document -> {
                    WeatherResponse cached = fromWeatherCache(document);
                    if (cached != null) {
                        weatherData.postValue(cached);
                        errorMsg.postValue(fallbackMessage);
                    } else {
                        weatherData.postValue(defaultWeather());
                        errorMsg.postValue("Weather cache is empty. Showing default local weather.");
                    }
                })
                .addOnFailureListener(error -> {
                    weatherData.postValue(defaultWeather());
                    errorMsg.postValue("Weather unavailable. Showing default local weather.");
                });
    }

    private WeatherResponse fromWeatherCache(DocumentSnapshot document) {
        if (document == null || !document.exists()) return null;

        Double temp = document.getDouble("temp");
        Long weatherCode = document.getLong("weatherCode");
        if (temp == null && weatherCode == null) return null;

        WeatherResponse response = new WeatherResponse();
        WeatherResponse.Current current = new WeatherResponse.Current();
        current.setTemperature(temp != null ? temp : 29.0);
        current.setFeelsLike(document.getDouble("feelsLike") != null ? document.getDouble("feelsLike") : current.getTemperature());
        current.setHumidity(document.getDouble("humidity") != null ? document.getDouble("humidity") : 75);
        current.setPrecipitation(document.getDouble("precipitation") != null ? document.getDouble("precipitation") : 0);
        current.setWindSpeed(document.getDouble("windSpeed") != null ? document.getDouble("windSpeed") : 0);
        current.setWeatherCode(weatherCode != null ? weatherCode.intValue() : 3);
        response.setCurrent(current);
        response.setDaily(defaultDaily(current));
        return response;
    }

    private WeatherResponse defaultWeather() {
        WeatherResponse response = new WeatherResponse();
        WeatherResponse.Current current = new WeatherResponse.Current();
        current.setTemperature(29.0);
        current.setFeelsLike(32.0);
        current.setHumidity(78);
        current.setPrecipitation(0);
        current.setWindSpeed(6.0);
        current.setWeatherCode(3);
        response.setCurrent(current);
        response.setDaily(defaultDaily(current));
        return response;
    }

    private WeatherResponse.Daily defaultDaily(WeatherResponse.Current current) {
        List<String> dates = new ArrayList<>();
        List<Integer> codes = new ArrayList<>();
        List<Double> max = new ArrayList<>();
        List<Double> min = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (int i = 0; i < 3; i++) {
            dates.add(format.format(calendar.getTime()));
            codes.add(current.getWeatherCode());
            max.add(current.getTemperature() + 2);
            min.add(Math.max(20, current.getTemperature() - 4));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        WeatherResponse.Daily daily = new WeatherResponse.Daily();
        daily.setTime(dates);
        daily.setWeatherCode(codes);
        daily.setTempMax(max);
        daily.setTempMin(min);
        return daily;
    }
}
