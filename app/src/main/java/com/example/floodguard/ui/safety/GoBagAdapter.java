package com.example.floodguard.ui.safety;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodguard.R;
import com.example.floodguard.model.GoBagItemModel;
import java.util.List;

public class GoBagAdapter extends RecyclerView.Adapter<GoBagAdapter.ViewHolder> {
    private List<GoBagItemModel> items;
    private OnItemCheckedChangeListener listener;

    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(GoBagItemModel item, boolean isChecked);
    }

    public GoBagAdapter(List<GoBagItemModel> items) {
        this.items = items;
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void submitList(List<GoBagItemModel> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gobag_check, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GoBagItemModel item = items.get(position);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setText(item.getName());
        holder.checkBox.setChecked(item.isChecked());
        holder.tvEssential.setText("Essential");
        holder.tvEssential.setVisibility(item.isEssential() ? View.VISIBLE : View.GONE);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            if (listener != null) {
                listener.onItemCheckedChanged(item, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvEssential;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Updated IDs to match item_gobag_check.xml
            checkBox = itemView.findViewById(R.id.checkboxItem);
            tvEssential = itemView.findViewById(R.id.tvItemLabel);
        }
    }
}
