package com.example.floodguard.ui.safety;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.floodguard.model.GoBagItemModel;
import com.example.floodguard.model.SafetyTipModel;
import com.example.floodguard.repository.SafetyRepository;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafetyViewModel extends AndroidViewModel {
    private final SafetyRepository repository;
    private final LiveData<List<SafetyTipModel>> safetyTips;
    private final LiveData<List<GoBagItemModel>> goBagItems;
    private final LiveData<List<String>> goBagProgress;
    private final MediatorLiveData<Map<String, List<SafetyTipModel>>> groupedTips = new MediatorLiveData<>();

    public SafetyViewModel(@NonNull Application application) {
        super(application);
        repository = SafetyRepository.getInstance();
        safetyTips = repository.getSafetyTips();
        goBagItems = repository.getGoBagItems();
        
        String uid = FirebaseAuth.getInstance().getUid();
        goBagProgress = repository.getGoBagProgress(uid != null ? uid : "");

        groupedTips.addSource(safetyTips, tips -> {
            if (tips != null) {
                Map<String, List<SafetyTipModel>> groups = new HashMap<>();
                for (SafetyTipModel tip : tips) {
                    List<SafetyTipModel> group = groups.get(tip.getCategory());
                    if (group == null) {
                        group = new ArrayList<>();
                        groups.put(tip.getCategory(), group);
                    }
                    group.add(tip);
                }
                groupedTips.setValue(groups);
            }
        });
    }

    public LiveData<Map<String, List<SafetyTipModel>>> getGroupedTips() { return groupedTips; }
    public LiveData<List<GoBagItemModel>> getGoBagItems() { return goBagItems; }
    public LiveData<List<String>> getGoBagProgress() { return goBagProgress; }

    public void updateGoBagItem(String itemId, boolean isChecked) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            repository.updateGoBagProgress(uid, itemId, isChecked);
        }
    }
}
