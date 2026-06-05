package com.example.floodguard.ui.emergency;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodguard.R;
import com.example.floodguard.repository.UserRepository;
import com.example.floodguard.ui.auth.LoginActivity;
import com.example.floodguard.ui.profile.ProfileActivity;
import com.example.floodguard.utils.PermissionUtils;
import com.example.floodguard.utils.UserDisplayUtils;
import com.google.firebase.auth.FirebaseAuth;

public class EmergencyFragment extends Fragment {

    private EmergencyViewModel viewModel;
    private EmergencyContactsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        viewModel = new ViewModelProvider(this).get(EmergencyViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.rv_emergency_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmergencyContactsAdapter();
        recyclerView.setAdapter(adapter);

        // Update User Initial in Top Bar
        TextView tvInitial = view.findViewById(R.id.tv_user_initial);
        tvInitial.setOnClickListener(this::showProfileMenu);
        
        UserRepository.getInstance().getCurrentUserData().observe(getViewLifecycleOwner(), user -> {
            tvInitial.setText(UserDisplayUtils.initial(user, FirebaseAuth.getInstance().getCurrentUser()));
        });

        viewModel.getContacts().observe(getViewLifecycleOwner(), contacts -> {
            adapter.submitList(contacts);
        });

        return view;
    }

    private void showProfileMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.getMenu().add(0, 1, 0, "My Profile");
        popup.getMenu().add(0, 3, 1, "Log Out");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                    return true;
                case 3:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
            }
            return false;
        });
        popup.show();
    }
}
