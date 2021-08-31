package com.flauschcode.broccoli.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Inject
    CategoryRepository categoryRepository;

    public static final String THEME_KEY = "theme";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        AndroidSupportInjection.inject(this);

        setPreferencesFromResource(R.xml.settings, rootKey);

        setUpSeasonalCalendarLanguagesPreference();
        setUpPreferredCategoryPreference();
        setUpDesignPreference();
    }

    private void setUpSeasonalCalendarLanguagesPreference() {
        MultiSelectListPreference seasonalCalendarLanguagesPreference = getPreferenceManager().findPreference("seasonal-calendar-languages");
        if (seasonalCalendarLanguagesPreference ==  null) {
            return;
        }

        seasonalCalendarLanguagesPreference.setSummaryProvider(preference -> {
            String selectedLanguages = String.join(", ", seasonalCalendarLanguagesPreference.getValues());
            return "".equals(selectedLanguages)? getString(R.string.no_language_set_message) : selectedLanguages;
        });
    }

    private void setUpPreferredCategoryPreference() {
        ListPreference preferredCategoryPreference = getPreferenceManager().findPreference("preferred-category");
        if (preferredCategoryPreference ==  null) {
            return;
        }

        List<Category> standardCategories = new ArrayList<>();
        standardCategories.add(categoryRepository.getAllRecipesCategory());
        standardCategories.add(categoryRepository.getSeasonalRecipesCategory());
        standardCategories.add(categoryRepository.getFavoritesCategory());

        preferredCategoryPreference.setEntries(standardCategories.stream().map(Category::getName).toArray(CharSequence[]::new));
        preferredCategoryPreference.setEntryValues(standardCategories.stream().map(Category::getCategoryId).map(String::valueOf).toArray(CharSequence[]::new));
    }

    private void setUpDesignPreference() {
        ListPreference themePreference = getPreferenceManager().findPreference(THEME_KEY);
        if (themePreference ==  null) {
            return;
        }

        themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String newString = newValue.toString();

            if (getString(R.string.MODE_NIGHT_NO).equals(newString)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (getString(R.string.MODE_NIGHT_YES).equals(newString)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (getString(R.string.MODE_NIGHT_FOLLOW_SYSTEM).equals(newString)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }

            return true;
        });
    }

}
