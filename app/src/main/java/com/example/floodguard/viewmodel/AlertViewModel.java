package com.example.floodguard.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.floodguard.model.Alert;
import com.example.floodguard.model.ChecklistItem;
import com.example.floodguard.repository.AlertRepository;
import com.example.floodguard.repository.ApiResult;
import java.util.List;

public class AlertViewModel extends ViewModel {
    private final AlertRepository repository = AlertRepository.getInstance();
    
    // Use the repository's LiveData directly to avoid manual observation and type issues
    public LiveData<ApiResult<List<Alert>>> getAlertsState() { 
        return repository.getActiveAlerts(); 
    }

    private final MutableLiveData<ApiResult<String>> _syncState = new MutableLiveData<>();
    public LiveData<ApiResult<String>> getSyncState() { return _syncState; }

    public void loadAlerts(String barangay, String severity) {
        // The repository automatically updates via Firestore SnapshotListener or Retrofit calls
    }

    public void syncChecklist(String userId, List<ChecklistItem> items) {
        // Implement Java sync logic here if needed
    }

    public void resetSyncState() {
        _syncState.setValue(null);
    }
}
