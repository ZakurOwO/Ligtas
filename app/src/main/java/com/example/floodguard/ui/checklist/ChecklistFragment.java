package com.example.floodguard.ui.checklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.floodguard.databinding.FragmentGobagChecklistBinding;
import com.example.floodguard.model.ChecklistItem;
import com.example.floodguard.repository.SafetyRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChecklistFragment extends Fragment {

    private FragmentGobagChecklistBinding binding;
    private ChecklistAdapter checklistAdapter;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final List<ChecklistItem> checklistItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGobagChecklistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateDefaultItems();
        setupRecyclerView();

        binding.btnSyncChecklist.setText("Save Checklist");
        binding.btnSyncChecklist.setOnClickListener(v -> syncToBackend());
    }

    private void populateDefaultItems() {
        checklistItems.clear();
        checklistItems.add(new ChecklistItem(null, "Drinking water for each person", "Food and Water", true, false));
        checklistItems.add(new ChecklistItem(null, "Ready-to-eat food for 24 hours", "Food and Water", true, false));
        checklistItems.add(new ChecklistItem(null, "First aid kit and maintenance medicines", "Health", true, false));
        checklistItems.add(new ChecklistItem(null, "IDs and documents in a waterproof pouch", "Documents", true, false));
        checklistItems.add(new ChecklistItem(null, "Fully charged phone and power bank", "Communication", true, false));
        checklistItems.add(new ChecklistItem(null, "Flashlight with extra batteries", "Tools", true, false));
        checklistItems.add(new ChecklistItem(null, "Turn off electricity if floodwater enters", "During Flood", true, false));
        checklistItems.add(new ChecklistItem(null, "Move family and valuables to higher ground", "During Flood", true, false));
        checklistItems.add(new ChecklistItem(null, "Avoid walking or driving through floodwater", "During Flood", true, false));
        checklistItems.add(new ChecklistItem(null, "Confirm evacuation route and nearest center", "Evacuation", true, false));
        checklistItems.add(new ChecklistItem(null, "Save emergency contacts and barangay hotlines", "Communication", true, false));
    }

    private void setupRecyclerView() {
        checklistAdapter = new ChecklistAdapter(checklistItems, (item, isChecked) -> {
            item.setChecked(isChecked);
            updateProgress();
        });
        binding.rvChecklist.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChecklist.setAdapter(checklistAdapter);
        updateProgress();
    }

    private void updateProgress() {
        int checked = 0;
        for (ChecklistItem item : checklistItems) {
            if (item.isChecked()) checked++;
        }
        int total = checklistItems.size();
        binding.progressChecklist.setMax(total);
        binding.progressChecklist.setProgress(checked);
        binding.tvProgress.setText(checked + " / " + total + " safety checks done");
    }

    private void syncToBackend() {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            for (ChecklistItem item : checklistItems) {
                SafetyRepository.getInstance().updateGoBagProgress(uid, item.getName(), item.isChecked());
            }
            Toast.makeText(getContext(), "Checklist saved.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
