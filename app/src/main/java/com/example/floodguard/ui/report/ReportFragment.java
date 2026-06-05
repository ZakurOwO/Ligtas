package com.example.floodguard.ui.report;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.floodguard.R;
import com.example.floodguard.model.UserModel;
import com.example.floodguard.repository.UserRepository;
import com.example.floodguard.ui.auth.LoginActivity;
import com.example.floodguard.ui.profile.ProfileActivity;
import com.example.floodguard.utils.PermissionUtils;
import com.example.floodguard.utils.UserDisplayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private static final String TAG = "ReportFragment";
    private ReportViewModel viewModel;
    private ImageView ivPhotoPreview;
    private Spinner spinnerBarangay;
    private EditText etDescription;
    private Button btnSubmit;
    private TextView tvUserInitial;
    private TextView tvLocationStatus;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean granted = result.containsValue(true);
                if (granted) {
                    getCurrentLocation();
                } else {
                    tvLocationStatus.setText(R.string.location_barangay);
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri uri = viewModel.getCameraImageUri();
                    if (uri != null) {
                        viewModel.setSelectedPhotoUri(uri);
                        displayImage(uri);
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    viewModel.setSelectedPhotoUri(uri);
                    displayImage(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        ivPhotoPreview = view.findViewById(R.id.iv_photo_preview);
        spinnerBarangay = view.findViewById(R.id.spinner_barangay);
        etDescription = view.findViewById(R.id.et_description);
        btnSubmit = view.findViewById(R.id.btn_submit_report);
        tvUserInitial = view.findViewById(R.id.tv_user_initial);
        tvLocationStatus = view.findViewById(R.id.tv_location_status);

        tvUserInitial.setOnClickListener(this::showProfileMenu);
        
        UserRepository.getInstance().getCurrentUserData().observe(getViewLifecycleOwner(), user -> {
            tvUserInitial.setText(UserDisplayUtils.initial(user, FirebaseAuth.getInstance().getCurrentUser()));
        });

        setupBarangaySpinner();
        checkLocationPermission();

        view.findViewById(R.id.upload_photo_area).setOnClickListener(v -> showImagePickerOptions());
        btnSubmit.setOnClickListener(v -> submitReport());

        observeViewModel();

        return view;
    }

    private void observeViewModel() {
        viewModel.getStatusMsg().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                if (msg.contains("successfully")) {
                    clearForm();
                }
            }
        });

        viewModel.getIsSubmitting().observe(getViewLifecycleOwner(), isSubmitting -> {
            btnSubmit.setEnabled(!isSubmitting);
            btnSubmit.setText(isSubmitting ? "Sending..." : "Send Report");
        });

        Uri selectedUri = viewModel.getSelectedPhotoUri().getValue();
        if (selectedUri != null) {
            displayImage(selectedUri);
        }
    }

    private void checkLocationPermission() {
        if (PermissionUtils.hasLocationPermission(requireContext())) {
            getCurrentLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        tvLocationStatus.setText(R.string.detecting_location);
        
        // Try last known location first for speed
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                applyDetectedLocation(location);
            }
            
            // Then request fresh location
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener(loc -> {
                        if (loc != null) {
                            applyDetectedLocation(loc);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Fresh location failed", e));
        });
    }

    private void applyDetectedLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        viewModel.setLocation(latitude, longitude);
        tvLocationStatus.setText(String.format(Locale.getDefault(), "Location detected: %.5f, %.5f", latitude, longitude));

        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locality = address.getSubLocality();
                if (locality == null || locality.trim().isEmpty()) locality = address.getLocality();
                if (locality == null || locality.trim().isEmpty()) locality = address.getFeatureName();
                if (locality != null && !locality.trim().isEmpty()) {
                    tvLocationStatus.setText("Location detected: " + locality);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            Log.w(TAG, "Reverse geocoding failed", e);
        }
    }

    private void showImagePickerOptions() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(getContext())
                .setTitle("Upload Flood Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (PermissionUtils.hasCameraPermission(requireContext())) {
                            openCamera();
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        }
                    } else {
                        galleryLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            Uri uri = FileProvider.getUriForFile(requireContext(), 
                    requireContext().getPackageName() + ".fileprovider", photoFile);
            viewModel.setCameraImageUri(uri);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
    }

    private void displayImage(Uri uri) {
        ivPhotoPreview.setImageURI(null);
        ivPhotoPreview.setImageURI(uri);
        ivPhotoPreview.setPadding(0, 0, 0, 0);
        ivPhotoPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        ViewGroup.LayoutParams lp = ivPhotoPreview.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        ivPhotoPreview.setLayoutParams(lp);
    }

    private void setupBarangaySpinner() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Select Barangay");
        barangays.add("Barangay 1");
        barangays.add("Barangay 2");
        barangays.add("Barangay 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, barangays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBarangay.setAdapter(adapter);
    }

    private void submitReport() {
        if (Boolean.TRUE.equals(viewModel.getIsSubmitting().getValue())) return;

        String description = etDescription.getText().toString().trim();
        String barangay = spinnerBarangay.getSelectedItem().toString();

        if (description.isEmpty() || barangay.equals("Select Barangay")) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserModel user = UserRepository.getInstance().getCurrentUserData().getValue();
        if (user == null) {
            UserRepository.getInstance().fetchCurrentUserRole();
            Toast.makeText(getContext(), "User data not loaded. Check connection.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String userName = user.getFullName() != null ? user.getFullName() : "User";
        viewModel.submitReport(barangay, description, "Moderate", userName);
    }

    private void clearForm() {
        etDescription.setText("");
        spinnerBarangay.setSelection(0);
        ivPhotoPreview.setImageResource(R.drawable.ic_photo_upload);
        ivPhotoPreview.setPadding(16, 16, 16, 16);
        viewModel.setSelectedPhotoUri(null);
        
        ViewGroup.LayoutParams lp = ivPhotoPreview.getLayoutParams();
        lp.width = (int) (56 * getResources().getDisplayMetrics().density);
        lp.height = (int) (56 * getResources().getDisplayMetrics().density);
        ivPhotoPreview.setLayoutParams(lp);
        
        getCurrentLocation();
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
