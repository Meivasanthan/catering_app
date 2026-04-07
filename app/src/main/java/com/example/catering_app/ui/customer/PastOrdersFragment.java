package com.example.catering_app.ui.customer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.catering_app.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

public class PastOrdersFragment extends Fragment {

    private Button btnWriteReview, btnReorder;
    private LinearLayout emptyState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_orders, container, false);

        btnWriteReview = view.findViewById(R.id.btnWriteReview);
        btnReorder = view.findViewById(R.id.btnReorderPast);
        emptyState = view.findViewById(R.id.emptyStatePast);

        // Write Review Button
        btnWriteReview.setOnClickListener(v -> showReviewDialog());

        // Reorder Button
        btnReorder.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Order placed successfully! 🎉", Toast.LENGTH_LONG).show();
        });

        Button btnBrowse = view.findViewById(R.id.btnBrowsePast);
        if (btnBrowse != null) {
            btnBrowse.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Browse caterers", Toast.LENGTH_SHORT).show();
            });
        }

        return view;
    }

    private void showReviewDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_write_review, null);
        bottomSheetDialog.setContentView(sheetView);

        RatingBar ratingBar = sheetView.findViewById(R.id.ratingBar);
        TextInputEditText etReview = sheetView.findViewById(R.id.etReview);
        Button btnSubmit = sheetView.findViewById(R.id.btnSubmitReview);
        TextView tvRatingHint = sheetView.findViewById(R.id.tvRatingHint);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (rating == 1) tvRatingHint.setText("Poor");
            else if (rating == 2) tvRatingHint.setText("Fair");
            else if (rating == 3) tvRatingHint.setText("Good");
            else if (rating == 4) tvRatingHint.setText("Very Good");
            else if (rating == 5) tvRatingHint.setText("Excellent!");
        });

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String review = etReview.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(getContext(), "Please rate your experience", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Thank you for your review! ⭐", Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
}