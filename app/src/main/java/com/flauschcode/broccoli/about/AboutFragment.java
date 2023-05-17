package com.flauschcode.broccoli.about;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class AboutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about, rootKey);

        Preference licensesPreference = findPreference("oss-licenses");
        if(licensesPreference != null){
            licensesPreference.setOnPreferenceClickListener(preference -> {
                new LibsBuilder().start(requireContext());
                return true;
            });
        }
    }
}
