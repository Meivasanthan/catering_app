package com.example.catering_app.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.catering_app.R;
import com.example.catering_app.adapters.OrdersPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrdersFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrdersPagerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // IMPORTANT: Pass requireActivity() not 'this'
        adapter = new OrdersPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Active");
                            break;
                        case 1:
                            tab.setText("Past");
                            break;
                        case 2:
                            tab.setText("Upcoming");
                            break;
                        case 3:
                            tab.setText("Cancelled");
                            break;
                    }
                }).attach();

        return view;
    }
}