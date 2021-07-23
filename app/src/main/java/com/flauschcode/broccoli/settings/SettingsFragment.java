package com.flauschcode.broccoli.settings;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.category.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        MultiSelectListPreference seasonalCalendarLanguagesPreference = getPreferenceManager().findPreference("seasonal-calendar-languages");
        seasonalCalendarLanguagesPreference.setSummaryProvider(preference -> {
            String selectedLanguages = seasonalCalendarLanguagesPreference.getValues().stream().collect(Collectors.joining(", "));
            return "".equals(selectedLanguages)? getString(R.string.no_language_set_message) : selectedLanguages;
        });

        ListPreference preferredCategoryPreference = getPreferenceManager().findPreference("preferred-category");

        List<Category> standardCategories = new ArrayList<>();
        standardCategories.add(Category.ALL);
        standardCategories.add(Category.SEASONAL);
        standardCategories.add(Category.FAVORITES);

        preferredCategoryPreference.setEntries(standardCategories.stream().map(Category::getName).toArray(CharSequence[]::new));
        preferredCategoryPreference.setEntryValues(standardCategories.stream().map(Category::getCategoryId).map(String::valueOf).toArray(CharSequence[]::new));
    }

}
