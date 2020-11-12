package com.flauschcode.broccoli.seasons;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flauschcode.broccoli.R;

import java.time.Month;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class MonthFragment extends Fragment {

    @Inject
    SeasonalCalendarHolder seasonalCalendarHolder;

    private final Month month;

    public MonthFragment(Month month) {
        super();
        this.month = month;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_month, container, false);

        seasonalCalendarHolder.get().ifPresent(seasonalCalendar -> {
            TextView textView = root.findViewById(R.id.month_text);
            textView.setText(seasonalCalendar.getSearchTermsFor(month).toString());
        });

        return root;
    }
}