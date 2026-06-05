package com.example.floodguard.ui.safety;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.floodguard.R;
import com.example.floodguard.repository.UserRepository;
import com.example.floodguard.ui.auth.LoginActivity;
import com.example.floodguard.ui.profile.ProfileActivity;
import com.example.floodguard.utils.UserDisplayUtils;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SafetyFragment extends Fragment {

    private SafetyViewModel viewModel;
    private ProgressBar pbGoBag;
    private TextView tvGoBagProgress;
    private TextView tvUserInitial;
    private TextView tvHeaderTitle;
    private TextView tvLastUpdated;
    private View topBar;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safety, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SafetyViewModel.class);

        pbGoBag = view.findViewById(R.id.pb_gobag);
        tvGoBagProgress = view.findViewById(R.id.tv_gobag_progress_text);
        tvUserInitial = view.findViewById(R.id.tv_user_initial);
        tvHeaderTitle = view.findViewById(R.id.tv_safety_header_title);
        tvLastUpdated = view.findViewById(R.id.tv_last_updated);
        topBar = view.findViewById(R.id.safety_top_bar);
        scrollView = view.findViewById(R.id.safety_scroll_view);

        tvUserInitial.setOnClickListener(this::showProfileMenu);

        // --- Polish: Smooth Sticky Header Elevation ---
        if (scrollView != null && topBar != null) {
            scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                float targetElevation = scrollY > 0 ? 8f : 0f;
                if (topBar.getElevation() != targetElevation) {
                    ObjectAnimator.ofFloat(topBar, "elevation", targetElevation).setDuration(200).start();
                }
            });
        }

        // Fetch and display user data with dynamic greeting and avatar initial
        UserRepository.getInstance().getCurrentUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getFullName() != null && !user.getFullName().isEmpty()) {
                String firstName = user.getFullName().split(" ")[0];
                updateGreeting(firstName);
                tvUserInitial.setText(UserDisplayUtils.initial(user, FirebaseAuth.getInstance().getCurrentUser()));
                tvUserInitial.animate().alpha(1f).setDuration(500).start();
            }
        });

        // Set up Go Bag items count
        viewModel.getGoBagItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                pbGoBag.setMax(items.size());
                updateProgressUI();
                updateLastUpdatedTimestamp();
            }
        });

        // Animate progress changes for a fluid feel
        viewModel.getGoBagProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) {
                animateProgressBar(progress.size());
            }
        });

        // Navigation to Checklist with micro-interactions
        view.findViewById(R.id.item_gobag).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            startPulseAnimation(v);
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.fragment_container, new GoBagChecklistFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Initialize and populate safety tip cards
        setupSafetyCard(view.findViewById(R.id.item_before_flood), 
                R.drawable.ic_shield, R.string.before_the_flood, R.string.before_flood_sub, "before_flood", R.drawable.bg_icon_blue);
        
        setupSafetyCard(view.findViewById(R.id.item_during_flood), 
                R.drawable.ic_siren, R.string.during_the_flood, R.string.during_flood_sub, "during_flood", R.drawable.bg_icon_red);
        
        setupSafetyCard(view.findViewById(R.id.item_after_flood), 
                R.drawable.ic_refresh, R.string.after_the_flood, R.string.after_flood_sub, "after_flood", R.drawable.bg_icon_teal);
        
        setupSafetyCard(view.findViewById(R.id.item_evacuation_guide), 
                R.drawable.ic_evacuation, R.string.evacuation_guide, R.string.evacuation_guide_sub, "evacuation", R.drawable.bg_icon_orange);
        
        setupSafetyCard(view.findViewById(R.id.item_water_safety), 
                R.drawable.ic_droplet, R.string.water_safety, R.string.water_safety_sub, "water_safety", R.drawable.bg_icon_purple);

        setupSafetyCard(view.findViewById(R.id.item_first_aid),
                R.drawable.ic_heart, R.string.first_aid_basics, R.string.first_aid_basics_sub, "first_aid", R.drawable.bg_icon_red);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animateEntrance();
    }

    private void animateEntrance() {
        View content = getView().findViewById(R.id.safety_content_layout);
        if (content instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) content;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                child.setAlpha(0f);
                child.setTranslationY(50f);
                child.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(100 + (i * 80))
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            }
        }
    }

    private void updateGreeting(String name) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) greeting = getString(R.string.good_morning, name);
        else if (hour >= 12 && hour < 17) greeting = getString(R.string.good_afternoon, name);
        else greeting = getString(R.string.good_evening, name);
        
        if (!greeting.equals(tvHeaderTitle.getText().toString())) {
            tvHeaderTitle.setAlpha(0f);
            tvHeaderTitle.setText(greeting);
            tvHeaderTitle.animate().alpha(1f).setDuration(500).start();
        }
    }

    private void animateProgressBar(int target) {
        if (pbGoBag == null) return;
        ObjectAnimator anim = ObjectAnimator.ofInt(pbGoBag, "progress", pbGoBag.getProgress(), target);
        anim.setDuration(800);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(animation -> updateProgressUI());
        anim.start();
    }

    private void startPulseAnimation(View view) {
        view.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .setDuration(100)
            .setInterpolator(new DecelerateInterpolator())
            .withEndAction(() -> 
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator(2.0f))
                    .start()
            ).start();
    }

    private void setupSafetyCard(View card, int iconRes, int titleRes, int subtitleRes, String category, int bgDrawable) {
        if (card == null) return;
        
        ImageView icon = card.findViewById(R.id.safety_icon);
        TextView title = card.findViewById(R.id.tv_safety_title);
        TextView subtitle = card.findViewById(R.id.tv_safety_subtitle);
        View iconContainer = card.findViewById(R.id.safety_icon_container);
        
        if (icon != null) icon.setImageResource(iconRes);
        if (title != null) title.setText(titleRes);
        if (subtitle != null) subtitle.setText(subtitleRes);
        if (iconContainer != null) iconContainer.setBackgroundResource(bgDrawable);
        
        card.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            startPulseAnimation(v);

            SafetyTipDetailFragment fragment = new SafetyTipDetailFragment();
            Bundle args = new Bundle();
            args.putString("category", category);
            fragment.setArguments(args);
            
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void updateProgressUI() {
        if (tvGoBagProgress == null || !isAdded()) return;

        int progress = pbGoBag.getProgress();
        int max = pbGoBag.getMax();

        if (progress == max && max > 0) {
            tvGoBagProgress.setText(R.string.go_bag_complete);
            tvGoBagProgress.setTextColor(ContextCompat.getColor(requireContext(), R.color.icon_green));
            pbGoBag.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.icon_green)));
        } else {
            tvGoBagProgress.setText(getString(R.string.go_bag_progress_format, progress, max));
            tvGoBagProgress.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            pbGoBag.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary)));
        }
    }

    private void updateLastUpdatedTimestamp() {
        if (tvLastUpdated != null && isAdded()) {
            String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
            tvLastUpdated.setText(getString(R.string.last_updated_format, time));
        }
    }
}
