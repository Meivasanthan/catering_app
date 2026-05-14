package com.example.catering_app.ui.customer;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.catering_app.LoginActivity;
import com.example.catering_app.R;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private ShapeableImageView profileImage;
    private TextView tvUserName, tvRating, tvReviews, tvTotalOrders, tvWallet, tvMemberYears, tvVersion, tvMembership;
    private Button btnUpgrade, btnEditPicture;
    private LinearLayout btnOrders, btnFav, btnAddress;
    private LinearLayout menuWallet, menuRefer, menuHelp, menuLegal, menuLogout;

    private SharedPreferences sharedPreferences;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        saveImageToStorage(imageUri);
                        loadProfileImage(imageUri);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && selectedImageUri != null) {
                    saveImageToStorage(selectedImageUri);
                    loadProfileImage(selectedImageUri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);

        initViews(view);
        loadUserData();
        setupClickListeners();
        loadProfileImageFromStorage();

        return view;
    }

    private void initViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvRating = view.findViewById(R.id.tvRating);
        tvReviews = view.findViewById(R.id.tvReviews);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvWallet = view.findViewById(R.id.tvWallet);
        tvMemberYears = view.findViewById(R.id.tvMemberYears);
        tvVersion = view.findViewById(R.id.tvVersion);
        tvMembership = view.findViewById(R.id.tvMembership);

        btnUpgrade = view.findViewById(R.id.btnUpgrade);
        btnEditPicture = view.findViewById(R.id.btnEditPicture);
        btnOrders = view.findViewById(R.id.btnOrders);
        btnFav = view.findViewById(R.id.btnFav);
        btnAddress = view.findViewById(R.id.btnAddress);
        menuWallet = view.findViewById(R.id.menuWallet);
        menuRefer = view.findViewById(R.id.menuRefer);
        menuHelp = view.findViewById(R.id.menuHelp);
        menuLegal = view.findViewById(R.id.menuLegal);
        menuLogout = view.findViewById(R.id.menuLogout);
    }

    private void loadUserData() {
        String name = sharedPreferences.getString("current_user_name", "John Doe");
        tvUserName.setText(name);
        tvRating.setText("4.8");
        tvReviews.setText("(342 reviews)");
        tvTotalOrders.setText("12");
        tvWallet.setText("₹0");
        tvMemberYears.setText("1");
        tvVersion.setText("Version 1.0.0");
        tvMembership.setText("Gold Member");
    }

    private void loadProfileImageFromStorage() {
        String imagePath = sharedPreferences.getString("profile_image_path", "");
        if (!imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Glide.with(this)
                        .load(imgFile)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.profile_photo_avr)
                                .error(R.drawable.profile_photo_avr))
                        .into(profileImage);
            }
        }
    }

    private void loadProfileImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(profileImage);
    }

    private void saveImageToStorage(Uri imageUri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            Bitmap resizedBitmap = resizeBitmap(bitmap, 500, 500);

            String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
            File directory = getActivity().getFilesDir();
            File file = new File(directory, fileName);

            FileOutputStream outputStream = new FileOutputStream(file);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.close();
            inputStream.close();

            sharedPreferences.edit().putString("profile_image_path", file.getAbsolutePath()).apply();

            Toast.makeText(getContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }

        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        new AlertDialog.Builder(getContext())
                .setTitle("Select Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Profile Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

        selectedImageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(getContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void setupClickListeners() {
        btnEditPicture.setOnClickListener(v -> showImagePickerDialog());

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

    // ========== FIXED LOGOUT METHOD ==========
    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // FIXED: ONLY change login status, DO NOT clear all data
                    SharedPreferences prefs = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("is_logged_in", false);  // Only change login status
                    editor.apply();  // Apply changes without deleting data

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
    // ========================================
}