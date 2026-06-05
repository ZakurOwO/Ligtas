package com.example.floodguard.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.data.DefaultMobileData;
import com.example.floodguard.model.EmergencyContactModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmergencyRepository {
    private static EmergencyRepository instance;
    private final FirebaseFirestore db;

    private EmergencyRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized EmergencyRepository getInstance() {
        if (instance == null) {
            instance = new EmergencyRepository();
        }
        return instance;
    }

    public LiveData<List<EmergencyContactModel>> getEmergencyContacts() {
        MutableLiveData<List<EmergencyContactModel>> contactsLiveData = new MutableLiveData<>();
        
        db.collection("emergencyContacts")
                .whereEqualTo("isActive", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        contactsLiveData.setValue(DefaultMobileData.emergencyContacts());
                        return;
                    }
                    if (value != null) {
                        List<EmergencyContactModel> contacts = new ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            EmergencyContactModel contact = doc.toObject(EmergencyContactModel.class);
                            contact.setContactId(doc.getId());
                            contacts.add(contact);
                        }
                        if (contacts.isEmpty()) {
                            contactsLiveData.setValue(DefaultMobileData.emergencyContacts());
                            return;
                        }
                        Collections.sort(contacts, (first, second) -> Integer.compare(first.getOrder(), second.getOrder()));
                        contactsLiveData.setValue(contacts);
                    }
                });

        return contactsLiveData;
    }
}
