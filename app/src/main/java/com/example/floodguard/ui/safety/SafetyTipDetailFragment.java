package com.example.floodguard.ui.safety;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodguard.R;
import com.example.floodguard.model.SafetyTipModel;
import java.util.ArrayList;
import java.util.List;

public class SafetyTipDetailFragment extends Fragment {

    private String category;
    private SafetyViewModel viewModel;
    private TipsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safety_tip_detail, container, false);

        if (getArguments() != null) {
            category = getArguments().getString("category");
        }

        viewModel = new ViewModelProvider(requireActivity()).get(SafetyViewModel.class);

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        TextView tvTitle = view.findViewById(R.id.tv_category_title);
        tvTitle.setText(formatCategoryTitle(category));

        RecyclerView recyclerView = view.findViewById(R.id.rv_tips);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TipsAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getGroupedTips().observe(getViewLifecycleOwner(), grouped -> {
            if (grouped != null && grouped.containsKey(category)) {
                adapter.submitList(grouped.get(category));
            }
        });

        return view;
    }

    private String formatCategoryTitle(String category) {
        if (category == null) return "Safety Tips";
        switch (category) {
            case "go_bag": return "Flood Safety Checklist";
            case "before_flood": return "Before the Flood";
            case "during_flood": return "During the Flood";
            case "after_flood": return "After the Flood";
            case "evacuation": return "Evacuation Guide";
            case "water_safety": return "Water Safety";
            case "first_aid": return "First Aid Basics";
            default: return "Safety Tips";
        }
    }

    private static class TipsAdapter extends androidx.recyclerview.widget.ListAdapter<SafetyTipModel, TipsAdapter.ViewHolder> {
        TipsAdapter() {
            super(new androidx.recyclerview.widget.DiffUtil.ItemCallback<SafetyTipModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull SafetyTipModel oldItem, @NonNull SafetyTipModel newItem) {
                    return oldItem.getTipId().equals(newItem.getTipId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull SafetyTipModel oldItem, @NonNull SafetyTipModel newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getContent().equals(newItem.getContent());
                }
            });
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_safety_tip_content, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SafetyTipModel tip = getItem(position);
            holder.tvTitle.setText(tip.getTitle());
            holder.tvContent.setText(tip.getContent());
        }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvContent;
            ViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_tip_title);
                tvContent = v.findViewById(R.id.tv_tip_content);
            }
        }
    }
}
