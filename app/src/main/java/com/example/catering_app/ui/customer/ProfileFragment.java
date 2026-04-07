package com.example.catering_app.ui.customer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.catering_app.LoginActivity;
import com.example.catering_app.R;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvRating, tvReviews, tvTotalOrders, tvWallet, tvMemberYears;
    private TextView tvProfileInitials;
    private Button btnEditProfile, btnUpgrade;
    private LinearLayout btnOrders, btnFav, btnAddress;
    private LinearLayout menuWallet, menuRefer, menuHelp, menuLegal, menuLogout;
    private View editPhotoContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadUserData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvRating = view.findViewById(R.id.tvRating);
        tvReviews = view.findViewById(R.id.tvReviews);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvWallet = view.findViewById(R.id.tvWallet);
        tvMemberYears = view.findViewById(R.id.tvMemberYears);
        tvProfileInitials = view.findViewById(R.id.tvProfileInitials);
        editPhotoContainer = view.findViewById(R.id.editPhotoContainer);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnUpgrade = view.findViewById(R.id.btnUpgrade);
        btnOrders = view.findViewById(R.id.btnOrders);
        btnFav = view.findViewById(R.id.btnFav);
        btnAddress = view.findViewById(R.id.btnAddress);
        menuWallet = view.findViewById(R.id.menuWallet);
        menuRefer = view.findViewById(R.id.menuRefer);
        menuHelp = view.findViewById(R.id.menuHelp);
        menuLegal = view.findViewById(R.id.menuLegal);
        menuLogout = view.findViewById(R.id.menuLogout);

        // Edit photo click
        if (editPhotoContainer != null) {
            editPhotoContainer.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Change Photo", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadUserData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String name = prefs.getString("user_name", "John Doe");
        tvUserName.setText(name.toUpperCase());
        tvRating.setText("4.8");
        tvReviews.setText("(342 reviews)");
        tvTotalOrders.setText("12");
        tvWallet.setText("₹0");
        tvMemberYears.setText("8");

        // Set initials for avatar
        if (tvProfileInitials != null && name.length() > 0) {
            String initials = name.substring(0, 1);
            if (name.contains(" ")) {
                int spaceIndex = name.indexOf(" ");
                if (spaceIndex + 1 < name.length()) {
                    initials = name.substring(0, 1) + name.substring(spaceIndex + 1, spaceIndex + 2);
                }
            }
            tvProfileInitials.setText(initials.toUpperCase());
        }
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(getContext(), "Edit Profile", Toast.LENGTH_SHORT).show());

        btnUpgrade.setOnClickListener(v ->
                Toast.makeText(getContext(), "Upgrade Membership", Toast.LENGTH_SHORT).show());

        btnOrders.setOnClickListener(v ->
                Toast.makeText(getContext(), "My Orders", Toast.LENGTH_SHORT).show());

        btnFav.setOnClickListener(v ->
                Toast.makeText(getContext(), "Favorites", Toast.LENGTH_SHORT).show());

        btnAddress.setOnClickListener(v ->
                Toast.makeText(getContext(), "Addresses", Toast.LENGTH_SHORT).show());

        menuWallet.setOnClickListener(v ->
                Toast.makeText(getContext(), "Wallet & Payments", Toast.LENGTH_SHORT).show());

        menuRefer.setOnClickListener(v ->
                Toast.makeText(getContext(), "Refer & Earn", Toast.LENGTH_SHORT).show());

        menuHelp.setOnClickListener(v ->
                Toast.makeText(getContext(), "Help & Support", Toast.LENGTH_SHORT).show());

        menuLegal.setOnClickListener(v ->
                Toast.makeText(getContext(), "Terms & Privacy", Toast.LENGTH_SHORT).show());

        menuLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences prefs = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}