package com.example.floodguard.ui.checklist;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.floodguard.databinding.ItemGobagCheckBinding;
import com.example.floodguard.model.ChecklistItem;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.CheckViewHolder> {

    public interface OnToggleListener {
        void onToggle(ChecklistItem item, boolean isChecked);
    }

    private final List<ChecklistItem> items;
    private final OnToggleListener    toggleListener;

    public ChecklistAdapter(List<ChecklistItem> items, OnToggleListener toggleListener) {
        this.items          = items;
        this.toggleListener = toggleListener;
    }

    @NonNull
    @Override
    public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGobagCheckBinding binding = ItemGobagCheckBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CheckViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CheckViewHolder extends RecyclerView.ViewHolder {

        private final ItemGobagCheckBinding binding;

        CheckViewHolder(ItemGobagCheckBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChecklistItem item) {
            binding.tvItemLabel.setText(item.getName());

            // Standard Java getter used here
            binding.checkboxItem.setOnCheckedChangeListener(null);
            binding.checkboxItem.setChecked(item.isChecked());

            if (item.isChecked()) {
                binding.tvItemLabel.setPaintFlags(
                        binding.tvItemLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                binding.tvItemLabel.setPaintFlags(
                        binding.tvItemLabel.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            binding.checkboxItem.setOnCheckedChangeListener((btn, isChecked) -> {
                toggleListener.onToggle(item, isChecked);
                notifyItemChanged(getAdapterPosition());
            });

            binding.getRoot().setOnClickListener(v -> binding.checkboxItem.toggle());
        }
    }
}
