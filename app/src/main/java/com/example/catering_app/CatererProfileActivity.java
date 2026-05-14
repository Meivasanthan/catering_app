package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import com.example.catering_app.models.UserData;

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
    private MaterialButton btnEditHours;

    // Account Information
    private TextView tvAccountName, tvAccountEmail, tvAccountPhone, tvAccountPassword, tvAccountRole;
    private TextView tvEditAccount;

    // Settings
    private SwitchMaterial switchNotifications, switchDarkMode, switchAutoAccept, switchLiveLocation;

    // Support Items
    private LinearLayout llHelpCenter, llContactSupport, llTermsConditions, llPrivacyPolicy;

    // Logout & Delete
    private MaterialButton btnLogout, btnDeleteAccount;

    private SharedPreferences sharedPreferences;
    private Animation bounceAnimation;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caterer_profile);
        // Initialize Bottom Navigation
        View bottomNav = findViewById(R.id.bottomNavigation);

        LinearLayout btnNavDashboard = bottomNav.findViewById(R.id.btnNavDashboard);
        LinearLayout btnNavMenu = bottomNav.findViewById(R.id.btnNavMenu);
        LinearLayout btnNavOrders = bottomNav.findViewById(R.id.btnNavOrders);
        LinearLayout btnNavEarnings = bottomNav.findViewById(R.id.btnNavEarnings);
        LinearLayout btnNavProfile = bottomNav.findViewById(R.id.btnNavProfile);

// Set click listeners
        btnNavDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatererDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        btnNavMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuManagementActivity.class);
            startActivity(intent);
            finish();
        });

        btnNavOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerOrdersActivity.class);
            startActivity(intent);
            finish();
        });

        btnNavEarnings.setOnClickListener(v -> {
            Intent intent = new Intent(this, EarningsActivity.class);
            startActivity(intent);
            finish();
        });

        btnNavProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatererProfileActivity.class);
            startActivity(intent);
            finish();
        });

        sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        initViews();
        setupToolbar();
        setupImagePicker();
        loadBusinessData();
        setupClickListeners();
        setupSwitches();
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
        btnEditHours = findViewById(R.id.btnEditHours);

        // Account Information
        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountEmail = findViewById(R.id.tvAccountEmail);
        tvAccountPhone = findViewById(R.id.tvAccountPhone);
        tvAccountPassword = findViewById(R.id.tvAccountPassword);
        tvAccountRole = findViewById(R.id.tvAccountRole);
        tvEditAccount = findViewById(R.id.tvEditAccount);

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

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ivBusinessLogo.setImageURI(imageUri);
                            ivBusinessLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            sharedPreferences.edit().putString("business_logo_uri", imageUri.toString()).apply();
                            Toast.makeText(this, "Logo updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void loadBusinessData() {
        // Load from SharedPreferences
        String businessName = sharedPreferences.getString("business_name", "Thambikalayan Caters");
        String email = sharedPreferences.getString("current_user_email", "thambikalayan@catering.com");
        String phone = sharedPreferences.getString("current_user_phone", "+91 98765 43210");
        String address = sharedPreferences.getString("business_address", "123 Food Street, New York");
        String gst = sharedPreferences.getString("gst_number", "27AAACT1234E1Z");
        String fullName = sharedPreferences.getString("current_user_name", "Thambikalayan");
        String role = sharedPreferences.getString("current_user_role", "Caterer");

        // Account Information
        tvAccountName.setText(fullName);
        tvAccountEmail.setText(email);
        tvAccountPhone.setText(phone);
        tvAccountPassword.setText("********");
        tvAccountRole.setText(role);

        // Business Information
        tvBusinessName.setText(businessName);
        tvBusinessAddress.setText(address);
        tvBusinessRating.setText("4.8 ★ (342 ratings)");

        tvBusinessInfoName.setText(businessName);
        tvBusinessInfoEmail.setText(email);
        tvBusinessInfoPhone.setText(phone);
        tvBusinessInfoGst.setText(gst);
        tvBusinessInfoAddress.setText(address);

        // Business Hours
        tvHoursWeekday.setText(sharedPreferences.getString("hours_weekday", "10:00 AM - 10:00 PM"));
        tvHoursSaturday.setText(sharedPreferences.getString("hours_saturday", "11:00 AM - 11:00 PM"));
        tvHoursSunday.setText(sharedPreferences.getString("hours_sunday", "12:00 PM - 08:00 PM"));

        // Stats
        tvTotalOrders.setText(sharedPreferences.getString("total_orders", "342"));
        tvTotalRating.setText(sharedPreferences.getString("total_rating", "4.8"));
        tvTotalRevenue.setText(sharedPreferences.getString("total_revenue", "₹45.2K"));

        updateBusinessStatus();

        // Load logo
        String logoUri = sharedPreferences.getString("business_logo_uri", "");
        if (!logoUri.isEmpty()) {
            try {
                ivBusinessLogo.setImageURI(Uri.parse(logoUri));
            } catch (Exception e) {
                ivBusinessLogo.setImageResource(R.drawable.app_logo);
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
        // Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            showEditProfileDialog();
        });

        // Edit Business Hours
        btnEditHours.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            showEditHoursDialog();
        });

        // Edit Business Information (click on info section)
        findViewById(R.id.cardBusinessInfo).setOnClickListener(v -> showEditProfileDialog());

        // ========== EDIT ACCOUNT (ADD THIS) ==========
        tvEditAccount.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            showEditAccountDialog();
        });
        // ============================================

        // Stats card clicks
        cardStatsOrders.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Total Orders: " + tvTotalOrders.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        cardStatsRating.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Rating: " + tvTotalRating.getText().toString() + " ⭐", Toast.LENGTH_SHORT).show();
        });

        cardStatsRevenue.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Revenue: " + tvTotalRevenue.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        // Logo click to change
        ivBusinessLogo.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            openImagePicker();
        });

        // Support clicks
        llHelpCenter.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            showHelpCenterDialog();
        });

        llContactSupport.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            showContactSupportDialog();
        });

        llTermsConditions.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            showTermsDialog();
        });

        llPrivacyPolicy.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            showPrivacyDialog();
        });

        // Logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Delete Account
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // ========== EDIT PROFILE DIALOG ==========
    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_business, null);

        TextInputEditText etBusinessName = dialogView.findViewById(R.id.etBusinessName);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        TextInputEditText etGst = dialogView.findViewById(R.id.etGst);
        TextInputEditText etAddress = dialogView.findViewById(R.id.etAddress);

        etBusinessName.setText(tvBusinessInfoName.getText().toString());
        etEmail.setText(tvBusinessInfoEmail.getText().toString());
        etPhone.setText(tvBusinessInfoPhone.getText().toString());
        etGst.setText(tvBusinessInfoGst.getText().toString());
        etAddress.setText(tvBusinessInfoAddress.getText().toString());

        builder.setTitle("Edit Business Information")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etBusinessName.getText().toString().trim();
                    String newEmail = etEmail.getText().toString().trim();
                    String newPhone = etPhone.getText().toString().trim();
                    String newGst = etGst.getText().toString().trim();
                    String newAddress = etAddress.getText().toString().trim();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("business_name", newName);
                    editor.putString("current_user_email", newEmail);
                    editor.putString("current_user_phone", newPhone);
                    editor.putString("gst_number", newGst);
                    editor.putString("business_address", newAddress);
                    editor.apply();

                    tvBusinessName.setText(newName);
                    tvBusinessInfoName.setText(newName);
                    tvBusinessInfoEmail.setText(newEmail);
                    tvBusinessInfoPhone.setText(newPhone);
                    tvBusinessInfoGst.setText(newGst);
                    tvBusinessInfoAddress.setText(newAddress);
                    tvBusinessAddress.setText(newAddress);
                    tvAccountName.setText(newName);
                    tvAccountEmail.setText(newEmail);
                    tvAccountPhone.setText(newPhone);

                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ========== EDIT BUSINESS HOURS DIALOG ==========
    private void showEditHoursDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_hours, null);

        TextInputEditText etWeekday = dialogView.findViewById(R.id.etWeekdayHours);
        TextInputEditText etSaturday = dialogView.findViewById(R.id.etSaturdayHours);
        TextInputEditText etSunday = dialogView.findViewById(R.id.etSundayHours);

        etWeekday.setText(tvHoursWeekday.getText().toString());
        etSaturday.setText(tvHoursSaturday.getText().toString());
        etSunday.setText(tvHoursSunday.getText().toString());

        builder.setTitle("Edit Business Hours")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String weekday = etWeekday.getText().toString().trim();
                    String saturday = etSaturday.getText().toString().trim();
                    String sunday = etSunday.getText().toString().trim();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("hours_weekday", weekday);
                    editor.putString("hours_saturday", saturday);
                    editor.putString("hours_sunday", sunday);
                    editor.apply();

                    tvHoursWeekday.setText(weekday);
                    tvHoursSaturday.setText(saturday);
                    tvHoursSunday.setText(sunday);

                    Toast.makeText(this, "Business hours updated!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ========== EDIT ACCOUNT DIALOG (ADD THIS METHOD) ==========
    private void showEditAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_account, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etAccountName);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etAccountEmail);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etAccountPhone);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etAccountPassword);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        // Load current values
        etName.setText(tvAccountName.getText().toString());
        etEmail.setText(tvAccountEmail.getText().toString());
        etPhone.setText(tvAccountPhone.getText().toString());

        builder.setTitle("Edit Account Information")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newEmail = etEmail.getText().toString().trim();
                    String newPhone = etPhone.getText().toString().trim();
                    String newPassword = etPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    // Validation
                    if (newName.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newEmail.isEmpty()) {
                        Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPhone.isEmpty()) {
                        Toast.makeText(this, "Phone cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Check if password is being changed
                    if (!newPassword.isEmpty()) {
                        if (!newPassword.equals(confirmPassword)) {
                            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (newPassword.length() < 6) {
                            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Save to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("current_user_name", newName);
                    editor.putString("current_user_email", newEmail);
                    editor.putString("current_user_phone", newPhone);

                    // Update password if changed
                    if (!newPassword.isEmpty()) {
                        editor.putString("current_user_password", newPassword);
                        updateUserPasswordInList(newEmail, newPassword);
                    }
                    editor.apply();

                    // Update UI
                    tvAccountName.setText(newName);
                    tvAccountEmail.setText(newEmail);
                    tvAccountPhone.setText(newPhone);
                    tvBusinessInfoName.setText(newName);
                    tvBusinessInfoEmail.setText(newEmail);
                    tvBusinessInfoPhone.setText(newPhone);
                    tvBusinessName.setText(newName);

                    Toast.makeText(this, "Account information updated!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Helper method to update password in users_list
    private void updateUserPasswordInList(String email, String newPassword) {
        String usersJson = sharedPreferences.getString("users_list", "");
        if (!usersJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<UserData>>() {}.getType();
            ArrayList<UserData> usersList = gson.fromJson(usersJson, type);

            for (UserData user : usersList) {
                if (user.getEmail().equals(email)) {
                    user.setPassword(newPassword);
                    break;
                }
            }

            String updatedJson = gson.toJson(usersList);
            sharedPreferences.edit().putString("users_list", updatedJson).apply();
        }
    }
    // ==========================================================

    // ========== SUPPORT DIALOGS ==========
    private void showHelpCenterDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Help Center")
                .setMessage("📖 How to manage your business\n\n"
                        + "• Add/Edit Menu Items\n"
                        + "• Accept/Decline Orders\n"
                        + "• Update Business Hours\n"
                        + "• View Earnings Reports\n\n"
                        + "For more help, contact support.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showContactSupportDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Contact Support")
                .setMessage("📞 Call us: +91 98765 43210\n"
                        + "📧 Email: support@feastly.com\n"
                        + "💬 Live Chat: Available 9 AM - 9 PM")
                .setPositiveButton("Call Now", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:+919876543210"));
                    startActivity(intent);
                })
                .setNeutralButton("Email", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:support@feastly.com"));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTermsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Terms & Conditions")
                .setMessage("📜 Terms of Service\n\n"
                        + "1. You agree to provide accurate business information\n"
                        + "2. You are responsible for order fulfillment\n"
                        + "3. Commission fees apply per order\n"
                        + "4. Payment settlements within 7 days\n"
                        + "5. We reserve the right to suspend accounts\n\n"
                        + "Last updated: January 2024")
                .setPositiveButton("I Agree", null)
                .show();
    }

    private void showPrivacyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Privacy Policy")
                .setMessage("🔒 Privacy Policy\n\n"
                        + "• We collect business and contact information\n"
                        + "• Your data is encrypted and secure\n"
                        + "• We do not share with third parties\n"
                        + "• You can request data deletion anytime\n\n"
                        + "For more details, contact our privacy team.")
                .setPositiveButton("OK", null)
                .show();
    }

    // ========== LOGOUT & DELETE ==========
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
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
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("⚠️ WARNING: This action cannot be undone!\n\n"
                        + "Deleting your account will:\n"
                        + "• Remove all your menu items\n"
                        + "• Cancel pending orders\n"
                        + "• Delete all business data\n"
                        + "• Remove earnings history\n\n"
                        + "Are you sure you want to proceed?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    sharedPreferences.edit().clear().apply();
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}