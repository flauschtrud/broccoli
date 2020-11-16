package com.flauschcode.broccoli.settings;

import android.os.Bundle;

import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;

import java.util.stream.Collectors;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        MultiSelectListPreference multiSelectListPreference = getPreferenceManager().findPreference("seasonal-calendar-languages");
        multiSelectListPreference.setSummaryProvider(preference -> multiSelectListPreference.getValues().stream().collect(Collectors.joining(", ")));
    }

}
