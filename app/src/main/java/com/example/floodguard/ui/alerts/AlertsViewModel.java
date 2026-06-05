package com.example.floodguard.ui.alerts;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.floodguard.model.Alert;
import com.example.floodguard.repository.AlertRepository;
import com.example.floodguard.repository.ApiResult;
import java.util.List;

public class AlertsViewModel extends AndroidViewModel {
    private final AlertRepository repository;
    private final LiveData<ApiResult<List<Alert>>> activeAlerts;

    public AlertsViewModel(@NonNull Application application) {
        super(application);
        repository = AlertRepository.getInstance();
        activeAlerts = repository.getActiveAlerts();
    }

    public LiveData<ApiResult<List<Alert>>> getActiveAlerts() {
        return activeAlerts;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cleanup();
    }
}
