package org.flauschhaus.broccoli.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.flauschhaus.broccoli.R;

import static android.app.Activity.RESULT_OK;

public class BackupAndRestoreFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.backup_and_restore, rootKey);

        Preference backupPreference = findPreference("backup");
        if(backupPreference != null){
            backupPreference.setOnPreferenceClickListener(preference -> {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_backup);
                return true;
            });
        }

        Preference restorePreference = findPreference("restore");
        if(restorePreference != null){
            restorePreference.setOnPreferenceClickListener(preference -> {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_restore);
                return true;
            });
        }
    }

    public static class BackupFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.backup, rootKey);

            Preference triggerBackup = findPreference("trigger_backup");
            triggerBackup.setOnPreferenceClickListener(preference -> {
                BackupService.enqueueWork(getContext(), new Intent(getActivity(), BackupService.class));
                return true;
            });
        }

    }

    public static class RestoreFragment extends PreferenceFragmentCompat {

        private static final int REQUEST_ARCHIVE_GET = 1;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.restore, rootKey);

            Preference triggerBackup = findPreference("trigger_restore");
            triggerBackup.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_ARCHIVE_GET);
                }
                return true;
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_ARCHIVE_GET && resultCode == RESULT_OK) {
                Intent intent = new Intent(getActivity(), RestoreService.class);
                intent.setData(data.getData());
                RestoreService.enqueueWork(getContext(), intent);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
