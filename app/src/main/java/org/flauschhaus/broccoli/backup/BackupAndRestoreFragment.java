package org.flauschhaus.broccoli.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.flauschhaus.broccoli.R;

public class BackupAndRestoreFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.backup_and_restore, rootKey);

        Preference serverPref = findPreference("backup");
        if(serverPref != null){
            serverPref.setOnPreferenceClickListener(preference -> {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_backup);
                return true;
            });
        }
    }

    public static class BackupFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.backup, rootKey);

            Preference myPref = findPreference("trigger_backup");
            myPref.setOnPreferenceClickListener(preference -> {
                BackupService.enqueueWork(getContext(), new Intent(getActivity(), BackupService.class));
                return true;
            });
        }

    }
}
