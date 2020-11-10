package com.flauschcode.broccoli.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

}
