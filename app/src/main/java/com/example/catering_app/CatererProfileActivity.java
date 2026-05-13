package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CatererProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivBack;
    private TextView tvToolbarTitle;

    // Profile Header
    private CardView cardProfile;
    private ImageView ivBusinessLogo;
    private TextView tvBusinessName, tvBusinessRating, tvBusinessAddress, tvBusinessStatus;
    private MaterialButton btnEditProfile;

    // Stats Cards
    private TextView tvTotalOrders, tvTotalRating, tvTotalRevenue;
    private CardView cardStatsOrders, cardStatsRating, cardStatsRevenue;

    // Business Information
    private TextView tvBusinessInfoName, tvBusinessInfoEmail, tvBusinessInfoPhone, tvBusinessInfoGst, tvBusinessInfoAddress;

    // Business Hours
    private TextView tvHoursWeekday, tvHoursSaturday, tvHoursSunday;

    // Settings
    private SwitchMaterial switchNotifications, switchDarkMode, switchAutoAccept, switchLiveLocation;

    // Support Items
    private LinearLayout llHelpCenter, llContactSupport, llTermsConditions, llPrivacyPolicy;

    // Logout & Delete
    private MaterialButton btnLogout, btnDeleteAccount;

    private SharedPreferences sharedPreferences;
    private Animation fadeIn, slideUp, bounceAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caterer_profile);

        sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        initViews();
        setupToolbar();
        loadBusinessData();
        setupClickListeners();
        setupSwitches();
        applyAnimations();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        // Profile Header
        cardProfile = findViewById(R.id.cardProfile);
        ivBusinessLogo = findViewById(R.id.ivBusinessLogo);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvBusinessRating = findViewById(R.id.tvBusinessRating);
        tvBusinessAddress = findViewById(R.id.tvBusinessAddress);
        tvBusinessStatus = findViewById(R.id.tvBusinessStatus);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // Stats Cards
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalRating = findViewById(R.id.tvTotalRating);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        cardStatsOrders = findViewById(R.id.cardStatsOrders);
        cardStatsRating = findViewById(R.id.cardStatsRating);
        cardStatsRevenue = findViewById(R.id.cardStatsRevenue);

        // Business Information
        tvBusinessInfoName = findViewById(R.id.tvBusinessInfoName);
        tvBusinessInfoEmail = findViewById(R.id.tvBusinessInfoEmail);
        tvBusinessInfoPhone = findViewById(R.id.tvBusinessInfoPhone);
        tvBusinessInfoGst = findViewById(R.id.tvBusinessInfoGst);
        tvBusinessInfoAddress = findViewById(R.id.tvBusinessInfoAddress);

        // Business Hours
        tvHoursWeekday = findViewById(R.id.tvHoursWeekday);
        tvHoursSaturday = findViewById(R.id.tvHoursSaturday);
        tvHoursSunday = findViewById(R.id.tvHoursSunday);

        // Settings
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchAutoAccept = findViewById(R.id.switchAutoAccept);
        switchLiveLocation = findViewById(R.id.switchLiveLocation);

        // Support
        llHelpCenter = findViewById(R.id.llHelpCenter);
        llContactSupport = findViewById(R.id.llContactSupport);
        llTermsConditions = findViewById(R.id.llTermsConditions);
        llPrivacyPolicy = findViewById(R.id.llPrivacyPolicy);

        // Buttons
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        tvToolbarTitle.setText("My Business");
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadBusinessData() {
        // Business Details
        String businessName = sharedPreferences.getString("business_name", "Thambikalayan Caters");
        String email = sharedPreferences.getString("current_user_email", "thambikalayan@catering.com");
        String phone = sharedPreferences.getString("current_user_phone", "+91 98765 43210");
        String address = sharedPreferences.getString("business_address", "123 Food Street, New York, NY 10001");

        tvBusinessName.setText(businessName);
        tvBusinessAddress.setText(address);
        tvBusinessRating.setText("4.8 ★ (342 ratings)");

        tvBusinessInfoName.setText(businessName);
        tvBusinessInfoEmail.setText(email);
        tvBusinessInfoPhone.setText(phone);
        tvBusinessInfoGst.setText("27AAACT1234E1Z");
        tvBusinessInfoAddress.setText(address);

        // Business Hours
        tvHoursWeekday.setText("10:00 AM - 10:00 PM");
        tvHoursSaturday.setText("11:00 AM - 11:00 PM");
        tvHoursSunday.setText("12:00 PM - 08:00 PM");

        // Check if open now
        updateBusinessStatus();

        // Stats
        tvTotalOrders.setText("342");
        tvTotalRating.setText("4.8");
        tvTotalRevenue.setText("₹45.2K");

        // Load logo if exists
        String logoPath = sharedPreferences.getString("business_logo_path", "");
        if (!logoPath.isEmpty()) {
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                Glide.with(this)
                        .load(logoFile)
                        .circleCrop()
                        .placeholder(R.drawable.app_logo)
                        .into(ivBusinessLogo);
            }
        }
    }

    private void updateBusinessStatus() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        boolean isOpen = true;
        String statusText = "Open Now";
        int statusColor = getColor(R.color.success);

        // Sunday check
        if (day == Calendar.SUNDAY) {
            if (hour < 12 || hour >= 20) {
                isOpen = false;
                statusText = "Closed Now";
                statusColor = getColor(R.color.warning);
            }
        } else if (day == Calendar.SATURDAY) {
            if (hour < 11 || hour >= 23) {
                isOpen = false;
                statusText = "Closed Now";
                statusColor = getColor(R.color.warning);
            }
        } else {
            if (hour < 10 || hour >= 22) {
                isOpen = false;
                statusText = "Closed Now";
                statusColor = getColor(R.color.warning);
            }
        }

        tvBusinessStatus.setText(statusText);
        tvBusinessStatus.setTextColor(statusColor);
    }

    private void setupSwitches() {
        // Load saved preferences
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        switchAutoAccept.setChecked(sharedPreferences.getBoolean("auto_accept", false));
        switchLiveLocation.setChecked(sharedPreferences.getBoolean("live_location", true));

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Notifications enabled" : "Notifications disabled", Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            Toast.makeText(this, "Dark mode will apply on restart", Toast.LENGTH_SHORT).show();
        });

        switchAutoAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("auto_accept", isChecked).apply();
            Toast.makeText(this, isChecked ? "Auto accept enabled" : "Auto accept disabled", Toast.LENGTH_SHORT).show();
        });

        switchLiveLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("live_location", isChecked).apply();
            Toast.makeText(this, isChecked ? "Location sharing enabled" : "Location sharing disabled", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
        });

        cardStatsOrders.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Total Orders: 342", Toast.LENGTH_SHORT).show();
        });

        cardStatsRating.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Average Rating: 4.8 ⭐", Toast.LENGTH_SHORT).show();
        });

        cardStatsRevenue.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Monthly Revenue: ₹45,280", Toast.LENGTH_SHORT).show();
        });

        // Support clicks
        llHelpCenter.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Help Center", Toast.LENGTH_SHORT).show();
        });

        llContactSupport.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+919876543210"));
            startActivity(intent);
        });

        llTermsConditions.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Terms & Conditions", Toast.LENGTH_SHORT).show();
        });

        llPrivacyPolicy.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_logged_in", false);
                    editor.apply();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyAnimations() {
        cardProfile.startAnimation(fadeIn);
        cardStatsOrders.startAnimation(slideUp);
        cardStatsRating.startAnimation(slideUp);
        cardStatsRevenue.startAnimation(slideUp);
    }
}