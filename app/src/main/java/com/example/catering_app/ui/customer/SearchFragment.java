package com.example.catering_app.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.catering_app.R;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private ImageView ivFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        ivFilter = view.findViewById(R.id.ivFilter);

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                Toast.makeText(getContext(), "Searching: " + query, Toast.LENGTH_SHORT).show();
                // TODO: Implement search functionality
            }
            return false;
        });

        ivFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filter dialog coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Show filter bottom sheet
        });

        return view;
    }
}