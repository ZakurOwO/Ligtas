package com.example.floodguard.ui.alerts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.floodguard.R;
import com.example.floodguard.databinding.FragmentAlertsBinding;
import com.example.floodguard.model.Alert;
import com.example.floodguard.repository.ApiResult;
import com.example.floodguard.repository.UserRepository;
import com.example.floodguard.ui.auth.LoginActivity;
import com.example.floodguard.ui.profile.ProfileActivity;
import com.example.floodguard.utils.UserDisplayUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AlertsFragment extends Fragment {

    private FragmentAlertsBinding binding;
    private AlertsViewModel viewModel;
    private AlertsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlertsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AlertsViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.tvUserInitial.setOnClickListener(this::showProfileMenu);
        UserRepository.getInstance().getCurrentUserData().observe(getViewLifecycleOwner(), user ->
                binding.tvUserInitial.setText(UserDisplayUtils.initial(user, FirebaseAuth.getInstance().getCurrentUser())));
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.getActiveAlerts());
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

    private void setupRecyclerView() {
        adapter = new AlertsAdapter();
        binding.rvAlerts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAlerts.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getActiveAlerts().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.status == ApiResult.Status.LOADING) {
                binding.swipeRefresh.setRefreshing(true);
            } else if (result.status == ApiResult.Status.SUCCESS) {
                binding.swipeRefresh.setRefreshing(false);
                List<Alert> alerts = result.data;
                if (alerts != null && !alerts.isEmpty()) {
                    adapter.submitList(alerts);
                    binding.layoutEmpty.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                }
            } else if (result.status == ApiResult.Status.ERROR) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
