package com.example.catering_app.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.catering_app.R;

public class CancelledOrdersFragment extends Fragment {

    private Button btnReorderCancelled;
    private LinearLayout orderCard;
    private LinearLayout emptyStateCancelled;

    // Change this to false to show empty state
    private boolean hasCancelledOrders = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancelled_orders, container, false);

        // Initialize views
        btnReorderCancelled = view.findViewById(R.id.btnReorderCancelled);
        orderCard = view.findViewById(R.id.orderCard);
        emptyStateCancelled = view.findViewById(R.id.emptyStateCancelled);

        // Show/hide based on data
        if (!hasCancelledOrders) {
            // No cancelled orders - show empty state
            if (orderCard != null) {
                orderCard.setVisibility(View.GONE);
            }
            if (emptyStateCancelled != null) {
                emptyStateCancelled.setVisibility(View.VISIBLE);
            }
        } else {
            // Has cancelled orders - hide empty state
            if (emptyStateCancelled != null) {
                emptyStateCancelled.setVisibility(View.GONE);
            }
        }

        // Set click listener for Order Again button
        if (btnReorderCancelled != null) {
            btnReorderCancelled.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Order again placed successfully! 🎉", Toast.LENGTH_LONG).show();
                // TODO: Add reorder functionality
            });
        }

        return view;
    }
}