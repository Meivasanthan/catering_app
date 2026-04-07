package com.example.catering_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private OnboardingAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoNavigate;
    private boolean isLastSlideReached = false;  // Track if user completed all slides

    private final int[] images = {
            R.drawable.onboarding_1,
            R.drawable.onboarding_2,
            R.drawable.onboarding_3
    };

    private final String[] titles = {
            "Discover Best Caterers",
            "Customize Your Menu",
            "Track Your Order"
    };

    private final String[] descriptions = {
            "Find top-rated caterers near you for any event.",
            "Choose your favorite dishes and get instant quote.",
            "Track your order in real-time from preparation to delivery."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initViews();
        setupViewPager();
        setupAutoNavigation();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setupViewPager() {
        adapter = new OnboardingAdapter(images, titles, descriptions);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> { }).attach();
    }

    private void setupAutoNavigation() {
        autoNavigate = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < images.length - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                    handler.postDelayed(this, 3000);
                } else if (!isLastSlideReached) {
                    // Only go to login when last slide is reached
                    isLastSlideReached = true;
                    goToLogin();
                }
            }
        };

        handler.postDelayed(autoNavigate, 3000);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(autoNavigate);
                handler.postDelayed(autoNavigate, 3000);

                // If user manually reaches last slide
                if (position == images.length - 1 && !isLastSlideReached) {
                    isLastSlideReached = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToLogin();
                        }
                    }, 2000);
                }
            }
        });
    }

    private void goToLogin() {
        // ONLY save onboarding as shown when user COMPLETES all slides
        getSharedPreferences("app_state", MODE_PRIVATE)
                .edit()
                .putBoolean("onboarding_shown", true)
                .apply();

        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(autoNavigate);
    }
}