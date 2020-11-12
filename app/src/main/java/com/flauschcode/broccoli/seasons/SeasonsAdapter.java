package com.flauschcode.broccoli.seasons;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.time.Month;

public class SeasonsAdapter extends FragmentStateAdapter {

    SeasonsAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        return new MonthFragment(Month.of(position + 1));
    }

    @Override
    public int getItemCount() {
        return Month.values().length;
    }
    
}
