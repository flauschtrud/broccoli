package com.flauschcode.broccoli.seasons;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.inject.Inject;

public class SeasonalCalendarHolder {

    private final Application application;

    private SeasonalCalendar seasonalCalendar;
    
    @Inject
    public SeasonalCalendarHolder(Application application) {
        this.application = application;
    }

    public Optional<SeasonalCalendar> get() {
        if (seasonalCalendar == null) {
            tryToBuildSeasonalCalendar();
        }
        
        return Optional.ofNullable(seasonalCalendar);
    }

    // TODO async preload
    private void tryToBuildSeasonalCalendar() {
        seasonalCalendar = null;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        String resourceName = sharedPreferences.getString("seasonal-calendar-region", null);
        if (resourceName == null) {
            Log.d(getClass().getName(), "No region has been selected yet.");
            return;
        }

        SeasonalCalendarJson seasonalCalendarJson;
        try {
            int resourceId = application.getResources().getIdentifier(resourceName, "raw", application.getPackageName());
            InputStream inputStream = application.getResources().openRawResource(resourceId);
            ObjectMapper objectMapper = new ObjectMapper();
            seasonalCalendarJson = objectMapper.readValue(inputStream, SeasonalCalendarJson.class);
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
            return;
        }

        seasonalCalendar = new SeasonalCalendar();
        seasonalCalendarJson.getFood().forEach(seasonalFoodJson -> {
            SeasonalFood seasonalFood = new SeasonalFood(getLocalizedNameFor(seasonalFoodJson.getName()), getSearchTermsFor(seasonalFoodJson.getName()), seasonalFoodJson.getMonths());
            seasonalFoodJson.getMonths().forEach(month -> seasonalCalendar.add(seasonalFood, month));
        });
    }

    private String getLocalizedNameFor(String seasonalFoodName) {
        return getLocalizedResource(seasonalFoodName, seasonalFoodName);
    }

    private String getSearchTermsFor(String seasonalFoodName) {
        return getLocalizedResource(seasonalFoodName + "_terms", seasonalFoodName);
    }

    private String getLocalizedResource(String resourceName, String fallback) {
        int resourceId = application.getResources().getIdentifier(resourceName, "string", application.getPackageName());
        try {
            return application.getString(resourceId);
        } catch (Resources.NotFoundException e) {
            Log.e(getClass().getName(), "Could not get localized resource for '" + resourceName + "'.");
        }
        return fallback;
    }

}
