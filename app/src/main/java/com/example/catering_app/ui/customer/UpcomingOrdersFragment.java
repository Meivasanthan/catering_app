package com.example.catering_app.ui.customer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.catering_app.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class UpcomingOrdersFragment extends Fragment {

    private Button btnReschedule, btnCancel, btnContact;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_orders, container, false);

        btnReschedule = view.findViewById(R.id.btnReschedule);
        btnCancel = view.findViewById(R.id.btnCancelUpcoming);
        btnContact = view.findViewById(R.id.btnContactCaterer);

        btnReschedule.setOnClickListener(v -> showRescheduleDialog());

        btnCancel.setOnClickListener(v -> showCancelConfirmationDialog());

        btnContact.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Connecting to caterer...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void showRescheduleDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_reschedule, null);
        bottomSheetDialog.setContentView(sheetView);

        DatePicker datePicker = sheetView.findViewById(R.id.datePicker);
        TimePicker timePicker = sheetView.findViewById(R.id.timePicker);
        Button btnConfirm = sheetView.findViewById(R.id.btnConfirmReschedule);
        Button btnCancel = sheetView.findViewById(R.id.btnCancelReschedule);

        // Set min date to today
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int hour, minute;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                String dateTime = day + "/" + month + "/" + year + " at " + hour + ":" + minute;
                Toast.makeText(getContext(), "Order rescheduled to " + dateTime, Toast.LENGTH_LONG).show();
                bottomSheetDialog.dismiss();
            });
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        // Close button
        TextView btnClose = sheetView.findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        bottomSheetDialog.show();
    }
    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    Toast.makeText(getContext(), "Order cancelled successfully", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}