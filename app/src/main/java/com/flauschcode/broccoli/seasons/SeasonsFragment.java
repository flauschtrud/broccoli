package com.flauschcode.broccoli.seasons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.flauschcode.broccoli.R;

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
        TextView textView = root.findViewById(R.id.seasons_text);
        textView.setText(seasonalCalendar.toString());

        return root;
    }
}
