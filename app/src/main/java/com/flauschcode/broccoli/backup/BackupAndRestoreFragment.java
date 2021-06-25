package com.flauschcode.broccoli.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;

public class BackupAndRestoreFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.backup_and_restore, rootKey);

        Preference backupPreference = findPreference("backup");
        if(backupPreference != null){
            backupPreference.setOnPreferenceClickListener(preference -> {
                BackupService.enqueueWork(getContext(), new Intent(getActivity(), BackupService.class));
                return true;
            });
        }

        Preference restorePreference = findPreference("restore");
        if(restorePreference != null){
            ActivityResultLauncher<String> getContentResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> new AlertDialog.Builder(getContext())
                            .setTitle(R.string.restore)
                            .setMessage(R.string.restore_alert_message)
                            .setNeutralButton(R.string.cancel, (dialog, id) -> {})
                            .setPositiveButton(R.string.restore_action, (dialog, id) -> {
                                Intent intent = new Intent(getActivity(), RestoreService.class);
                                intent.setData(uri);
                                RestoreService.enqueueWork(getContext(), intent);
                            }).show());

            restorePreference.setOnPreferenceClickListener(preference -> {
                getContentResultLauncher.launch("application/*");
                return true;
            });
        }
    }

}
