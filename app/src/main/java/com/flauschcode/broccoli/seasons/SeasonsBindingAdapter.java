package com.flauschcode.broccoli.seasons;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SeasonsBindingAdapter {

    private SeasonalCalendarHolder seasonalCalendarHolder;

    @Inject
    public SeasonsBindingAdapter(SeasonalCalendarHolder seasonalCalendarHolder) {
        this.seasonalCalendarHolder = seasonalCalendarHolder;
    }

    @BindingAdapter(value = {"ingredients"}, requireAll = false)
    public void bind(ImageView imageView, String ingredients) {
        Optional<SeasonalCalendar> seasonalCalendarOptional = seasonalCalendarHolder.get();
        if (seasonalCalendarOptional.isPresent()) {
            SeasonalCalendar seasonalCalendar = seasonalCalendarOptional.get();
            Pattern pattern = Pattern.compile(seasonalCalendar.getSearchTermsForCurrentMonth().stream().collect(Collectors.joining("|")));
            Matcher matcher = pattern.matcher(ingredients);
            if (matcher.find()) {
                imageView.setVisibility(View.VISIBLE);
                return;
            }
        }
        imageView.setVisibility(View.GONE);
    }

}
