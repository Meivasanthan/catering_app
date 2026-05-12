package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.catering_app.ui.customer.FavoritesFragment;
import com.example.catering_app.ui.customer.HomeFragment;
import com.example.catering_app.ui.customer.OrdersFragment;
import com.example.catering_app.ui.customer.ProfileFragment;
import com.example.catering_app.ui.customer.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CHECK: If user is Owner, redirect to Owner Dashboard
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String userRole = prefs.getString("current_user_role", "Customer");

        if (userRole != null && userRole.equals("Caterer")) {
            // Owner should not be here - redirect to Owner Dashboard
            Intent intent = new Intent(MainActivity.this, CatererDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Load Home Fragment by default
        loadFragment(new HomeFragment());

        // Bottom Navigation Item Selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            }
            else if (itemId == R.id.nav_search) {
                loadFragment(new SearchFragment());
                return true;
            }
            else if (itemId == R.id.nav_orders) {
                loadFragment(new OrdersFragment());
                return true;
            }
            else if (itemId == R.id.nav_favorites) {
                loadFragment(new FavoritesFragment());
                return true;
            }
            else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.commit();
    }
}