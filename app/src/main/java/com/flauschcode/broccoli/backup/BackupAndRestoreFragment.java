package com.flauschcode.broccoli.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;

import static android.app.Activity.RESULT_OK;

public class BackupAndRestoreFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_ARCHIVE_GET = 1;

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
            restorePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_ARCHIVE_GET);
                }
                return true;
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ARCHIVE_GET && resultCode == RESULT_OK) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.restore)
                    .setMessage(R.string.restore_alert_message)
                    .setNeutralButton(R.string.cancel, (dialog, id) -> {})
                    .setPositiveButton(R.string.restore_action, (dialog, id) -> {
                        Intent intent = new Intent(getActivity(), RestoreService.class);
                        intent.setData(data.getData());
                        RestoreService.enqueueWork(getContext(), intent);
                    }).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
