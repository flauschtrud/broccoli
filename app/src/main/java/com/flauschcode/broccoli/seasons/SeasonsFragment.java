package com.flauschcode.broccoli.seasons;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.flauschcode.broccoli.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;

public class SeasonsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_seasons, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!sharedPreferences.getBoolean("seasonal-calendar-enabled", false)) {
            root.findViewById(R.id.seasons_text).setVisibility(View.VISIBLE);
            return root;
        }

        SeasonsAdapter seasonsAdapter = new SeasonsAdapter(this);
        ViewPager2 viewPager = root.findViewById(R.id.seasons_pager);
        viewPager.setAdapter(seasonsAdapter);

        TabLayout tabLayout = root.findViewById(R.id.seasons_tablayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(Month.of(position+1).getDisplayName(TextStyle.FULL_STANDALONE, getResources().getConfiguration().getLocales().get(0)))).attach();

        viewPager.setCurrentItem(LocalDate.now().getMonth().getValue()-1, false);

        return root;
    }
}
