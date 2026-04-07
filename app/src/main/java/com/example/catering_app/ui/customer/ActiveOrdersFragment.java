package com.example.catering_app.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.catering_app.R;

public class ActiveOrdersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_orders, container, false);

        // Track Order Button
        Button btnTrackOrder = view.findViewById(R.id.btnTrackOrder);
        btnTrackOrder.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening track order map...", Toast.LENGTH_SHORT).show();
            // TODO: Open Order Tracking Activity
        });

        // Contact Button
        Button btnContact = view.findViewById(R.id.btnContact);
        btnContact.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Contacting caterer...", Toast.LENGTH_SHORT).show();
            // TODO: Open chat or call
        });

        // Reorder Button
        Button btnReorder = view.findViewById(R.id.btnReorder);
        btnReorder.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Reorder placed successfully!", Toast.LENGTH_SHORT).show();
            // TODO: Reorder logic
        });

        Button btnBrowse = view.findViewById(R.id.btnBrowse);
        if (btnBrowse != null) {
            btnBrowse.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Browse caterers", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to home
            });
        }

        return view;
    }
}