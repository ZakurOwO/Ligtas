package com.example.floodguard.ui.emergency;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.floodguard.model.EmergencyContactModel;
import com.example.floodguard.repository.EmergencyRepository;
import java.util.List;

public class EmergencyViewModel extends AndroidViewModel {
    private final EmergencyRepository repository;
    private final LiveData<List<EmergencyContactModel>> contacts;

    public EmergencyViewModel(@NonNull Application application) {
        super(application);
        repository = EmergencyRepository.getInstance();
        contacts = repository.getEmergencyContacts();
    }

    public LiveData<List<EmergencyContactModel>> getContacts() {
        return contacts;
    }
}
