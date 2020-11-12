package com.flauschcode.broccoli.seasons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.flauschcode.broccoli.R;
import com.google.android.material.tabs.TabLayout;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SeasonsFragment extends Fragment {

    @Inject
    SeasonalCalendarHolder seasonalCalendarHolder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_seasons, container, false);

        SeasonalCalendar seasonalCalendar = seasonalCalendarHolder.get().orElse(new SeasonalCalendar());

        TabLayout tabLayout = root.findViewById(R.id.tablayout_calendar);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        Arrays.stream(Month.values()).forEach(month -> {
            tabLayout.addTab(tabLayout.newTab().setText(String.valueOf(month.getDisplayName(TextStyle.FULL_STANDALONE, getResources().getConfiguration().getLocales().get(0)))));
        });

        return root;
    }
}
