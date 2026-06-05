package com.example.floodguard.ui.emergency;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodguard.R;
import com.example.floodguard.model.EmergencyContactModel;

public class EmergencyContactsAdapter extends ListAdapter<EmergencyContactModel, EmergencyContactsAdapter.ContactViewHolder> {

    public EmergencyContactsAdapter() {
        super(new DiffUtil.ItemCallback<EmergencyContactModel>() {
            @Override
            public boolean areItemsTheSame(@NonNull EmergencyContactModel oldItem, @NonNull EmergencyContactModel newItem) {
                return oldItem.getContactId().equals(newItem.getContactId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull EmergencyContactModel oldItem, @NonNull EmergencyContactModel newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getNumber().equals(newItem.getNumber()) &&
                        oldItem.getIconColor().equals(newItem.getIconColor());
            }
        });
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvNumber;
        private final ImageView ivIcon;
        private final View iconContainer, btnCall;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            tvNumber = itemView.findViewById(R.id.tv_contact_number);
            ivIcon = itemView.findViewById(R.id.contact_icon);
            iconContainer = itemView.findViewById(R.id.contact_icon_container);
            btnCall = itemView.findViewById(R.id.btn_call);
        }

        public void bind(EmergencyContactModel contact) {
            tvName.setText(contact.getName());
            tvNumber.setText(contact.getNumber());

            // Bind Icon Color
            int colorRes = R.color.icon_purple;
            int bgRes = R.drawable.bg_icon_purple;
            
            if ("green".equals(contact.getIconColor())) {
                colorRes = R.color.icon_green;
                bgRes = R.drawable.bg_icon_green;
            } else if ("blue".equals(contact.getIconColor())) {
                colorRes = R.color.icon_blue;
                bgRes = R.drawable.bg_icon_blue;
            } else if ("orange".equals(contact.getIconColor())) {
                colorRes = R.color.icon_orange;
                bgRes = R.drawable.bg_icon_orange;
            } else if ("red".equals(contact.getIconColor())) {
                colorRes = R.color.icon_red;
                bgRes = R.drawable.bg_icon_red;
            }

            iconContainer.setBackground(ContextCompat.getDrawable(itemView.getContext(), bgRes));
            ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), colorRes));

            // Bind Icon based on type
            int iconRes = R.drawable.ic_shield;
            switch (contact.getType()) {
                case "mdrrmo": iconRes = R.drawable.ic_shield_check; break;
                case "barangay_hall": iconRes = R.drawable.ic_building; break;
                case "pnp": iconRes = R.drawable.ic_siren; break;
                case "bfp": iconRes = R.drawable.ic_flame; break;
            }
            ivIcon.setImageResource(iconRes);

            btnCall.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getNumber()));
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
