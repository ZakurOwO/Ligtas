package com.example.floodguard.ui.weather;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.floodguard.model.WeatherResponse;
import com.example.floodguard.repository.WeatherRepository;

public class WeatherViewModel extends AndroidViewModel {
    private final WeatherRepository repository;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        repository = WeatherRepository.getInstance();
    }

    public LiveData<WeatherResponse> getWeather() {
        return repository.getWeather();
    }

    public LiveData<String> getErrorMsg() {
        return repository.getErrorMsg();
    }

    public void refreshWeather(double lat, double lng) {
        repository.fetchWeather(lat, lng);
    }
}
