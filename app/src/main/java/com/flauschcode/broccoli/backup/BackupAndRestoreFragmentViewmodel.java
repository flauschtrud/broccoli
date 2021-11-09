package com.flauschcode.broccoli.backup;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.Optional;

public class BackupAndRestoreFragmentViewmodel extends ViewModel {

    private Uri exportUri;

    public void setExportUri(Uri exportedRecipe) {
        this.exportUri = exportedRecipe;
    }

    public Optional<Uri> getExportUri() {
        return Optional.ofNullable(exportUri);
    }

}
