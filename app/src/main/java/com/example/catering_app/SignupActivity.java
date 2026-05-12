package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.example.catering_app.models.UserData;

public class SignupActivity extends AppCompatActivity {

    private TextView tvBack;
    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private EditText etBusinessName, etGstNumber, etBusinessAddress, etCuisineType;
    private RadioGroup rgRole;
    private RadioButton rbCustomer, rbCaterer;
    private Button btnCreateAccount;
    private TextView tvLogin, tvShowPassword, tvShowConfirmPassword;
    private LinearLayout ownerExtraFields;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        setupClickListeners();
        setupPasswordToggle();
        setupRoleBasedFields();
    }

    private void initViews() {
        tvBack = findViewById(R.id.tvBack);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgRole = findViewById(R.id.rgRole);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbCaterer = findViewById(R.id.rbCaterer);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvLogin = findViewById(R.id.tvLogin);
        tvShowPassword = findViewById(R.id.tvShowPassword);
        tvShowConfirmPassword = findViewById(R.id.tvShowConfirmPassword);
        ownerExtraFields = findViewById(R.id.ownerExtraFields);

        // Owner extra fields
        etBusinessName = findViewById(R.id.etBusinessName);
        etGstNumber = findViewById(R.id.etGstNumber);
        etBusinessAddress = findViewById(R.id.etBusinessAddress);
        etCuisineType = findViewById(R.id.etCuisineType);

        // Set default selection
        rbCustomer.setChecked(true);
        ownerExtraFields.setVisibility(View.GONE);
    }

    private void setupRoleBasedFields() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCaterer) {
                // Show owner extra fields
                ownerExtraFields.setVisibility(View.VISIBLE);
            } else {
                // Hide owner extra fields
                ownerExtraFields.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());

        btnCreateAccount.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            int selectedRoleId = rgRole.getCheckedRadioButtonId();
            String role = (selectedRoleId == R.id.rbCustomer) ? "Customer" : "Caterer";

            // Get owner extra fields if role is Caterer
            String businessName = "", gstNumber = "", businessAddress = "", cuisineType = "";
            if (role.equals("Caterer")) {
                businessName = etBusinessName.getText().toString().trim();
                gstNumber = etGstNumber.getText().toString().trim();
                businessAddress = etBusinessAddress.getText().toString().trim();
                cuisineType = etCuisineType.getText().toString().trim();

                // Validate owner fields
                if (businessName.isEmpty()) {
                    etBusinessName.setError("Business name is required");
                    etBusinessName.requestFocus();
                    return;
                }
                if (businessAddress.isEmpty()) {
                    etBusinessAddress.setError("Business address is required");
                    etBusinessAddress.requestFocus();
                    return;
                }
            }

            if (validateInputs(fullName, email, phone, password, confirmPassword)) {
                if (isEmailAlreadyExists(email)) {
                    Toast.makeText(this, "Email already registered! Please login.", Toast.LENGTH_LONG).show();
                } else {
                    saveUserData(fullName, email, phone, password, role, businessName, gstNumber, businessAddress, cuisineType);
                    Toast.makeText(this, "Account created successfully! Please login.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void setupPasswordToggle() {
        tvShowPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tvShowPassword.setText("👁️");
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tvShowPassword.setText("🙈");
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.length());
        });

        tvShowConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tvShowConfirmPassword.setText("👁️");
                isConfirmPasswordVisible = false;
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tvShowConfirmPassword.setText("🙈");
                isConfirmPasswordVisible = true;
            }
            etConfirmPassword.setSelection(etConfirmPassword.length());
        });
    }

    private boolean validateInputs(String fullName, String email, String phone,
                                   String password, String confirmPassword) {
        // Full Name validation
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }
        if (fullName.length() < 3) {
            etFullName.setError("Full name must be at least 3 characters");
            etFullName.requestFocus();
            return false;
        }

        // Email validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email (e.g., name@example.com)");
            etEmail.requestFocus();
            return false;
        }

        // Phone validation
        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }
        if (phone.length() != 10) {
            etPhone.setError("Phone number must be exactly 10 digits");
            etPhone.requestFocus();
            return false;
        }
        if (!TextUtils.isDigitsOnly(phone)) {
            etPhone.setError("Phone number must contain only digits");
            etPhone.requestFocus();
            return false;
        }

        // Password validation
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() > 20) {
            etPassword.setError("Password must be less than 20 characters");
            etPassword.requestFocus();
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        if (!hasLetter || !hasDigit) {
            etPassword.setError("Password must contain both letters and numbers");
            etPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isEmailAlreadyExists(String email) {
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String usersJson = prefs.getString("users_list", "");
        if (usersJson.isEmpty()) return false;

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<UserData>>() {}.getType();
        ArrayList<UserData> usersList = gson.fromJson(usersJson, type);

        for (UserData user : usersList) {
            if (user.getEmail() != null && user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void saveUserData(String fullName, String email, String phone, String password,
                              String role, String businessName, String gstNumber,
                              String businessAddress, String cuisineType) {
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String usersJson = prefs.getString("users_list", "");

        Gson gson = new Gson();
        ArrayList<UserData> usersList;

        if (usersJson.isEmpty()) {
            usersList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<UserData>>() {}.getType();
            usersList = gson.fromJson(usersJson, type);
        }

        UserData newUser = new UserData(fullName, email, phone, password, role,
                businessName, gstNumber, businessAddress, cuisineType);
        usersList.add(newUser);

        String newUsersJson = gson.toJson(usersList);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("users_list", newUsersJson);
        editor.apply();
    }
}