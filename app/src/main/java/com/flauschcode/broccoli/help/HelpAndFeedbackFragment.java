package com.flauschcode.broccoli.help;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class HelpAndFeedbackFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.help_and_feedback, rootKey);

        Preference licensesPreference = findPreference("oss-licenses");
        if(licensesPreference != null){
            licensesPreference.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireActivity(), OssLicensesMenuActivity.class));
                return true;
            });
        }
    }
}
