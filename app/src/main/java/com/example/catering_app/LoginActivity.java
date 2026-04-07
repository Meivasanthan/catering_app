package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.example.catering_app.models.UserData;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    private LinearLayout btnGoogle, btnFacebook, btnApple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnApple = findViewById(R.id.btnApple);
    }

    private void setupClickListeners() {
        // Login button
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                performLogin(email, password);
            }
        });

        // Forgot password
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Forgot Password? Contact support", Toast.LENGTH_LONG).show();
        });

        // Sign up link
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Social login buttons (placeholder)
        btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Google Login coming soon", Toast.LENGTH_SHORT).show());
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Facebook Login coming soon", Toast.LENGTH_SHORT).show());
        btnApple.setOnClickListener(v -> Toast.makeText(this, "Apple Login coming soon", Toast.LENGTH_SHORT).show());
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performLogin(String email, String password) {
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String usersJson = prefs.getString("users_list", "");

        if (usersJson.isEmpty()) {
            Toast.makeText(this, "No account found! Please sign up first.", Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<UserData>>() {}.getType();
        ArrayList<UserData> usersList = gson.fromJson(usersJson, type);

        UserData loggedInUser = null;
        for (UserData user : usersList) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                loggedInUser = user;
                break;
            }
        }

        if (loggedInUser != null) {
            // Save current logged in user
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putString("user_email", email);
            editor.putString("user_name", loggedInUser.getFullName());
            editor.putString("user_phone", loggedInUser.getPhone());
            editor.putString("user_role", loggedInUser.getRole());
            editor.apply();

            Toast.makeText(this, "Welcome " + loggedInUser.getFullName() + "!", Toast.LENGTH_SHORT).show();

            // Go to Home
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_LONG).show();
        }
    }
}