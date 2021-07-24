package com.flauschcode.broccoli.backup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.flauschcode.broccoli.R;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class BackupAndRestoreFragment extends PreferenceFragmentCompat {

    @Inject
    BackupService backupService;

    @Inject
    RestoreService restoreService;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        AndroidSupportInjection.inject(this);

        setPreferencesFromResource(R.xml.backup_and_restore, rootKey);

        Preference backupPreference = findPreference("backup");
        if(backupPreference != null){
            backupPreference.setOnPreferenceClickListener(preference -> {
                backup();
                return true;
            });
        }

        Preference restorePreference = findPreference("restore");
        if(restorePreference != null){
            ActivityResultLauncher<String> getContentResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null) {
                            return;
                        }

                        new AlertDialog.Builder(requireContext())
                                .setTitle(R.string.restore)
                                .setMessage(R.string.restore_all_question)
                                .setNeutralButton(android.R.string.cancel, (dialog, id) -> {
                                })
                                .setPositiveButton(android.R.string.ok, (dialog, id) -> restoreFrom(uri))
                                .show();
                    });

            restorePreference.setOnPreferenceClickListener(preference -> {
                getContentResultLauncher.launch("application/*");
                return true;
            });
        }
    }

    private void backup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.backup_recipes)
                .setView(getLayoutInflater().inflate(R.layout.backup_and_restore_progress, null))
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        ProgressBar progressBar = alertDialog.findViewById(R.id.backup_progress);
        backupService.getMaxRecipes().observe(getViewLifecycleOwner(), progressBar::setMax);
        backupService.getCount().observe(getViewLifecycleOwner(), progressBar::setProgress);

        backupService.backup()
                .thenAccept(uri -> {
                        alertDialog.dismiss();
                        showChooser(uri);
                })
                .exceptionally(e -> {
                    Log.e(getClass().getName(), e.getMessage());
                    alertDialog.dismiss();
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), getString(R.string.backup_failed_message), Toast.LENGTH_LONG).show());
                    return null;
                });
    }

    private void showChooser(Uri archiveUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, archiveUri);
        shareIntent.setType("application/broccoli-archive");
        shareIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(shareIntent, null);
        List<ResolveInfo> resInfoList = requireActivity().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            requireActivity().grantUriPermission(packageName, archiveUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(chooser);
    }

    private void restoreFrom(Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.restore_recipes)
                .setView(getLayoutInflater().inflate(R.layout.backup_and_restore_progress, null))
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
        ProgressBar progressBar = alertDialog.findViewById(R.id.backup_progress);
        progressBar.setIndeterminate(true);

        restoreService.restore(uri)
                .thenAccept(count -> {
                    alertDialog.dismiss();
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), getString(R.string.restore_completed_message, count), Toast.LENGTH_LONG).show());
                })
                .exceptionally(e -> {
                    Log.e(getClass().getName(), e.getMessage());
                    alertDialog.dismiss();
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), getString(R.string.restore_failed_message), Toast.LENGTH_LONG).show());
                    return null;
                });
    }

}
