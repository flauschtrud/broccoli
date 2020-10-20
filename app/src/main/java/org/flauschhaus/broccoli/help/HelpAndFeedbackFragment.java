package org.flauschhaus.broccoli.help;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import org.flauschhaus.broccoli.R;

public class HelpAndFeedbackFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.help_and_feedback, rootKey);
    }
}
