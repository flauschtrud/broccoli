package com.flauschcode.broccoli.seasons;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SeasonalCalendarHolder implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final Application application;

    private SeasonalCalendar seasonalCalendar;
    
    public SeasonalCalendarHolder(Application application) {
        this.application = application;
        PreferenceManager.getDefaultSharedPreferences(application).registerOnSharedPreferenceChangeListener(this);
    }

    // TODO work with LiveData
    public Optional<SeasonalCalendar> get() {
        if (seasonalCalendar == null) {
            Log.d(getClass().getName(), "Try to build seasonal calendar because it is still null.");
            tryToBuildSeasonalCalendar();
        }
        
        return Optional.ofNullable(seasonalCalendar);
    }

    private synchronized void tryToBuildSeasonalCalendar() {
        if (seasonalCalendar != null) {
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        String resourceName = sharedPreferences.getString("seasonal-calendar-region", null);
        if (resourceName == null) {
            Log.d(getClass().getName(), "No region has been selected yet.");
            return;
        }

        Set<String> languages = sharedPreferences.getStringSet("seasonal-calendar-languages", new HashSet<>());
        if (languages.isEmpty()) {
            Log.d(getClass().getName(), "No languages have been selected yet.");
            return;
        }

        List<Resources> resources = new ArrayList<>();
        languages.forEach(language -> {
            Configuration configuration = new Configuration(application.getResources().getConfiguration());
            configuration.setLocale(Locale.forLanguageTag(language));
            resources.add(application.createConfigurationContext(configuration).getResources());
        });

        SeasonalCalendarJson seasonalCalendarJson;
        int resourceId = application.getResources().getIdentifier(resourceName, "raw", application.getPackageName());
        try (InputStream inputStream = application.getResources().openRawResource(resourceId)) {
            ObjectMapper objectMapper = new ObjectMapper();
            seasonalCalendarJson = objectMapper.readValue(inputStream, SeasonalCalendarJson.class);
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
            return;
        }

        seasonalCalendar = new SeasonalCalendar();
        seasonalCalendarJson.getFood().forEach(seasonalFoodJson -> {
            SeasonalFood seasonalFood = new SeasonalFood(getLocalizedNameFor(seasonalFoodJson.getName()), getSearchTermsFor(seasonalFoodJson.getName(), resources), seasonalFoodJson.getMonths());
            seasonalFoodJson.getMonths().forEach(month -> seasonalCalendar.add(seasonalFood, month));
        });
    }

    private String getLocalizedNameFor(String seasonalFoodName) {
        return getLocalizedResource(seasonalFoodName, seasonalFoodName);
    }

    private String getSearchTermsFor(String seasonalFoodName, List<Resources> resources) {
        return resources.stream()
                .map(resource -> getLocalizedResource(resource, seasonalFoodName + "_terms", seasonalFoodName))
                .collect(Collectors.joining(", "));
    }

    private String getLocalizedResource(String resourceName, String fallback) {
        return getLocalizedResource(application.getResources(), resourceName, fallback);
    }

    private String getLocalizedResource(Resources resources, String resourceName, String fallback) {
        int resourceId = resources.getIdentifier(resourceName, "string", application.getPackageName());
        try {
            return resources.getString(resourceId);
        } catch (Resources.NotFoundException e) {
            Log.e(getClass().getName(), "Could not get localized resource for '" + resourceName + "'.");
        }
        return fallback;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("seasonal-calendar-region") || s.equals("seasonal-calendar-languages")) {
            Log.d(getClass().getName(), "Reloading seasonal calendar...");
            seasonalCalendar = null;
            CompletableFuture.runAsync(this::tryToBuildSeasonalCalendar);
        }
    }
}
