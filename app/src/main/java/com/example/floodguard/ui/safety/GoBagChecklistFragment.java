package com.example.floodguard.ui.safety;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodguard.R;
import com.example.floodguard.model.GoBagItemModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoBagChecklistFragment extends Fragment {

    private SafetyViewModel viewModel;
    private GoBagAdapter adapter;
    private List<GoBagItemModel> latestItems = new ArrayList<>();
    private List<String> latestProgress = new ArrayList<>();
    private ProgressBar progressChecklist;
    private TextView tvProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gobag_checklist, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SafetyViewModel.class);

        ImageView btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        progressChecklist = view.findViewById(R.id.progressChecklist);
        tvProgress = view.findViewById(R.id.tvProgress);

        // Updated ID to match fragment_gobag_checklist.xml
        RecyclerView recyclerView = view.findViewById(R.id.rvChecklist);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            
            // Note: The adapter constructor in GoBagAdapter might need updating if it doesn't match this callback
            adapter = new GoBagAdapter(new java.util.ArrayList<>()); 
            adapter.setOnItemCheckedChangeListener((item, isChecked) -> {
                    viewModel.updateGoBagItem(item.getItemId(), isChecked);
                    item.setChecked(isChecked);
                    updateProgress();
            });
            recyclerView.setAdapter(adapter);
        }

        viewModel.getGoBagItems().observe(getViewLifecycleOwner(), items -> {
            latestItems = items != null ? items : new ArrayList<>();
            renderChecklist();
        });

        viewModel.getGoBagProgress().observe(getViewLifecycleOwner(), progress -> {
            latestProgress = progress != null ? progress : new ArrayList<>();
            renderChecklist();
        });

        return view;
    }

    private void renderChecklist() {
        if (adapter == null || latestItems == null) return;
        Set<String> checkedIds = new HashSet<>(latestProgress);
        List<GoBagItemModel> rendered = new ArrayList<>();
        for (GoBagItemModel item : latestItems) {
            item.setChecked(checkedIds.contains(item.getItemId()));
            rendered.add(item);
        }
        adapter.submitList(rendered);
        updateProgress();
    }

    private void updateProgress() {
        if (progressChecklist == null || tvProgress == null || latestItems == null) return;
        int checked = 0;
        for (GoBagItemModel item : latestItems) {
            if (item.isChecked()) checked++;
        }
        int total = latestItems.size();
        progressChecklist.setMax(total);
        progressChecklist.setProgress(checked);
        tvProgress.setText(checked + " / " + total + " safety checks done");
    }
}
