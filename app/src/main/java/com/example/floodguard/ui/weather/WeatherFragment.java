package com.example.floodguard.ui.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.floodguard.R;
import com.example.floodguard.model.WeatherResponse;
import com.example.floodguard.repository.UserRepository;
import com.example.floodguard.ui.auth.LoginActivity;
import com.example.floodguard.ui.profile.ProfileActivity;
import com.example.floodguard.utils.UserDisplayUtils;
import com.example.floodguard.utils.WeatherCodeUtils;
import com.google.firebase.auth.FirebaseAuth;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    private WeatherViewModel viewModel;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvTemp, tvCondition, tvFeelsLike, tvLastUpdated, tvLocation;
    private TextView tvUserInitial;
    
    private TextView tvHumidity, tvWind, tvRainfall, tvFloodRisk;
    
    // Forecast views
    private View[] forecastRows = new View[3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        swipeRefresh = view.findViewById(R.id.swipe_refresh_weather);
        tvTemp = view.findViewById(R.id.tv_temperature);
        tvCondition = view.findViewById(R.id.tv_condition);
        tvFeelsLike = view.findViewById(R.id.tv_feels_like);
        tvLastUpdated = view.findViewById(R.id.tv_last_updated);
        tvLocation = view.findViewById(R.id.tv_location_name);
        tvUserInitial = view.findViewById(R.id.tv_user_initial);
        
        tvUserInitial.setOnClickListener(this::showProfileMenu);
        UserRepository.getInstance().getCurrentUserData().observe(getViewLifecycleOwner(), user ->
                tvUserInitial.setText(UserDisplayUtils.initial(user, FirebaseAuth.getInstance().getCurrentUser())));

        View cardHum = view.findViewById(R.id.card_humidity);
        tvHumidity = cardHum.findViewById(R.id.tv_weather_value);
        ((ImageView)cardHum.findViewById(R.id.iv_weather_icon)).setImageResource(R.drawable.ic_droplet);
        ((TextView)cardHum.findViewById(R.id.tv_weather_label)).setText("Humidity");

        View cardWnd = view.findViewById(R.id.card_wind);
        tvWind = cardWnd.findViewById(R.id.tv_weather_value);
        ((ImageView)cardWnd.findViewById(R.id.iv_weather_icon)).setImageResource(R.drawable.ic_weather);
        ((TextView)cardWnd.findViewById(R.id.tv_weather_label)).setText("Wind Speed");

        View cardRain = view.findViewById(R.id.card_precipitation);
        tvRainfall = cardRain.findViewById(R.id.tv_weather_value);
        ((ImageView)cardRain.findViewById(R.id.iv_weather_icon)).setImageResource(R.drawable.ic_droplet);
        ((TextView)cardRain.findViewById(R.id.tv_weather_label)).setText("Rainfall");

        View cardRisk = view.findViewById(R.id.card_flood_risk);
        tvFloodRisk = cardRisk.findViewById(R.id.tv_weather_value);
        ((ImageView)cardRisk.findViewById(R.id.iv_weather_icon)).setImageResource(R.drawable.ic_warning_triangle);
        ((TextView)cardRisk.findViewById(R.id.tv_weather_label)).setText("Flood Risk");

        forecastRows[0] = view.findViewById(R.id.forecast_friday);
        forecastRows[1] = view.findViewById(R.id.forecast_saturday);
        forecastRows[2] = view.findViewById(R.id.forecast_sunday);

        view.findViewById(R.id.btn_refresh_weather).setOnClickListener(v -> refreshWeather());
        swipeRefresh.setOnRefreshListener(this::refreshWeather);

        viewModel.getWeather().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });

        refreshWeather();
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

    private void refreshWeather() {
        swipeRefresh.setRefreshing(true);
        viewModel.refreshWeather(14.5995, 120.9842); // Manila
    }

    private void updateUI(WeatherResponse weather) {
        swipeRefresh.setRefreshing(false);
        if (weather == null) return;

        if (weather.getCurrent() != null) {
            WeatherResponse.Current current = weather.getCurrent();
            tvTemp.setText(String.format(Locale.getDefault(), "%.0f°C", current.getTemperature()));
            tvCondition.setText(WeatherCodeUtils.getCondition(current.getWeatherCode()));
            tvFeelsLike.setText(String.format(Locale.getDefault(), "Feels like %.0f°C", current.getFeelsLike()));
            tvLastUpdated.setText("Last updated: " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
            tvHumidity.setText(current.getHumidity() + "%");
            tvWind.setText(String.format(Locale.getDefault(), "%.1f km/h", current.getWindSpeed()));
            tvRainfall.setText(String.format(Locale.getDefault(), "%.1f mm", current.getPrecipitation()));
            tvFloodRisk.setText(WeatherCodeUtils.getSeverity(current.getWeatherCode()));
        }

        if (weather.getDaily() != null
                && weather.getDaily().getTime() != null
                && weather.getDaily().getWeatherCode() != null
                && weather.getDaily().getTempMax() != null
                && weather.getDaily().getTempMin() != null) {
            WeatherResponse.Daily daily = weather.getDaily();
            int forecastCount = Math.min(
                    Math.min(daily.getTime().size(), daily.getWeatherCode().size()),
                    Math.min(daily.getTempMax().size(), daily.getTempMin().size())
            );
            for (int i = 0; i < forecastRows.length && i < forecastCount; i++) {
                View row = forecastRows[i];
                TextView tvDay = row.findViewById(R.id.tv_forecast_day);
                TextView tvCond = row.findViewById(R.id.tv_forecast_condition);
                TextView tvTempMinMax = row.findViewById(R.id.tv_forecast_temp);

                String dateStr = daily.getTime().get(i);
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
                    tvDay.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(date));
                } catch (ParseException e) {
                    tvDay.setText(dateStr);
                }

                tvCond.setText(WeatherCodeUtils.getCondition(daily.getWeatherCode().get(i)));
                tvTempMinMax.setText(String.format(Locale.getDefault(), "%.0f° / %.0f°", 
                        daily.getTempMax().get(i), daily.getTempMin().get(i)));
            }
        }
    }
}
