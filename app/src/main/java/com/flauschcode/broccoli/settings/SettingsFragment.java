package com.flauschcode.broccoli.settings;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.recipe.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Inject
    RecipeRepository recipeRepository;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        AndroidSupportInjection.inject(this);

        setPreferencesFromResource(R.xml.settings, rootKey);

        MultiSelectListPreference seasonalCalendarLanguagesPreference = getPreferenceManager().findPreference("seasonal-calendar-languages");
        seasonalCalendarLanguagesPreference.setSummaryProvider(preference -> {
            String selectedLanguages = seasonalCalendarLanguagesPreference.getValues().stream().collect(Collectors.joining(", "));
            return "".equals(selectedLanguages)? getString(R.string.no_language_set_message) : selectedLanguages;
        });

        ListPreference preferredCategoryPreference = getPreferenceManager().findPreference("preferred-category");

        List<Category> standardCategories = new ArrayList<>();
        standardCategories.add(recipeRepository.getCategoryAll());
        standardCategories.add(recipeRepository.getCategorySeasonal());
        standardCategories.add(recipeRepository.getCategoryFavorites());

        preferredCategoryPreference.setEntries(standardCategories.stream().map(Category::getName).toArray(CharSequence[]::new));
        preferredCategoryPreference.setEntryValues(standardCategories.stream().map(Category::getCategoryId).map(String::valueOf).toArray(CharSequence[]::new));
    }

}
