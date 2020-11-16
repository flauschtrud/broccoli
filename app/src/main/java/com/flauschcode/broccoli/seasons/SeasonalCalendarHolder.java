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
import java.util.stream.Collectors;

import javax.inject.Inject;

// TODO not really singleton yet
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

        List<Resources> resources = new ArrayList<>();
        Set<String> languages = sharedPreferences.getStringSet("seasonal-calendar-languages", new HashSet<>());
        languages.forEach(language -> {
            Configuration configuration = new Configuration(application.getResources().getConfiguration());
            configuration.setLocale(new Locale(language));
            resources.add(application.createConfigurationContext(configuration).getResources());
        });

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

}
