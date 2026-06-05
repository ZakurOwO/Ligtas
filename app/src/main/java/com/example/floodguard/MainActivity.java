package com.example.floodguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.floodguard.repository.UserRepository;
import com.example.floodguard.ui.alerts.AlertsFragment;
import com.example.floodguard.ui.emergency.EmergencyFragment;
import com.example.floodguard.ui.report.ReportFragment;
import com.example.floodguard.ui.safety.SafetyFragment;
import com.example.floodguard.ui.weather.WeatherFragment;
import com.example.floodguard.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private View navCard;
    private View fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Fetch user data globally so fragments can use it
        UserRepository.getInstance().fetchCurrentUserRole();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        View root = findViewById(R.id.main);
        navCard = findViewById(R.id.bottom_nav_card);
        bottomNav = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);

        ViewCompat.setOnApplyWindowInsetsListener(navCard, (v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });
        
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) navCard.getLayoutParams();
            mlp.leftMargin = systemBars.left;
            mlp.rightMargin = systemBars.right;
            mlp.bottomMargin = systemBars.bottom;
            navCard.setLayoutParams(mlp);

            int navTotalHeight = (int) (64 * getResources().getDisplayMetrics().density) + systemBars.bottom;
            fragmentContainer.setPadding(0, 0, 0, navTotalHeight);
            
            return insets;
        });

        bottomNav.setOnItemSelectedListener(item -> {
            bottomNav.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_alerts) selectedFragment = new AlertsFragment();
            else if (itemId == R.id.nav_report) selectedFragment = new ReportFragment();
            else if (itemId == R.id.nav_emergency) selectedFragment = new EmergencyFragment();
            else if (itemId == R.id.nav_safety) selectedFragment = new SafetyFragment();
            else if (itemId == R.id.nav_weather) selectedFragment = new WeatherFragment();

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_alerts);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
