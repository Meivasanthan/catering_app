package com.example.catering_app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.catering_app.ui.customer.ActiveOrdersFragment;
import com.example.catering_app.ui.customer.PastOrdersFragment;
import com.example.catering_app.ui.customer.UpcomingOrdersFragment;
import com.example.catering_app.ui.customer.CancelledOrdersFragment;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    public OrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ActiveOrdersFragment();
            case 1:
                return new PastOrdersFragment();
            case 2:
                return new UpcomingOrdersFragment();
            case 3:
                return new CancelledOrdersFragment();
            default:
                return new ActiveOrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}