package com.example.floodguard.ui.alerts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodguard.R;
import com.example.floodguard.model.Alert;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {
    private List<Alert> alerts = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());

    public void submitList(List<Alert> newAlerts) {
        this.alerts = newAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alert alert = alerts.get(position);
        holder.tvTitle.setText(alert.getTitle());
        
        // Corrected getters to match the Alert.java model
        holder.tvDescription.setText(alert.getMessage());
        holder.tvBarangay.setText(alert.getLocation());
        holder.tvSeverity.setText(alert.getType());
        
        // Format Firebase Timestamp to readable String
        Timestamp createdAt = alert.getCreatedAt();
        if (createdAt != null) {
            holder.tvTimestamp.setText(dateFormat.format(createdAt.toDate()));
        } else {
            holder.tvTimestamp.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvBarangay, tvSeverity, tvTimestamp;

        ViewHolder(View itemView) {
            super(itemView);
            // Matched to item_alert_card.xml IDs
            tvTitle = itemView.findViewById(R.id.tvAlertTitle);
            tvDescription = itemView.findViewById(R.id.tvAlertDescription);
            tvBarangay = itemView.findViewById(R.id.tvBarangayTag);
            tvSeverity = itemView.findViewById(R.id.tvSeverity);
            tvTimestamp = itemView.findViewById(R.id.tvAlertTimestamp);
        }
    }
}
