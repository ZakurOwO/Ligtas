package com.example.floodguard.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private static UserRepository instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final MutableLiveData<UserModel> currentUserData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    private UserRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public FirebaseAuth getAuth() { return auth; }
    public MutableLiveData<UserModel> getCurrentUserData() { return currentUserData; }
    public MutableLiveData<String> getErrorMsg() { return errorMsg; }

    public void fetchCurrentUserRole() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            fetchFromFirestore(user.getUid());
        } else {
            currentUserData.setValue(null);
            errorMsg.setValue("User is not signed in");
        }
    }

    private void fetchFromFirestore(String uid) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                    currentUserData.postValue(userModel);
                } else {
                    errorMsg.postValue("User document not found");
                }
            })
            .addOnFailureListener(e -> errorMsg.postValue(e.getMessage()));
    }

    public void logout() {
        auth.signOut();
        currentUserData.setValue(null);
    }
}
