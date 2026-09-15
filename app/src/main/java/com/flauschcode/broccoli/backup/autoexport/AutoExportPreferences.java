package com.flauschcode.broccoli.backup.autoexport;

import android.content.SharedPreferences;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AutoExportPreferences {

    static final String KEY_ENABLED = "auto-export-enabled";
    static final String KEY_DIRECTORY_URI = "auto-export-directory-uri";
    static final String KEY_LAST_EXPORT_DATE = "last-auto-export-date";

    private final SharedPreferences sharedPreferences;

    @Inject
    public AutoExportPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isEnabled() {
        return sharedPreferences.getBoolean(KEY_ENABLED, false);
    }

    public void setEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    public Optional<String> getExportDirectoryUri() {
        return Optional.ofNullable(sharedPreferences.getString(KEY_DIRECTORY_URI, null));
    }

    public void setExportDirectoryUri(String uri) {
        sharedPreferences.edit().putString(KEY_DIRECTORY_URI, uri).apply();
    }

    public void saveLastExportDate(String localizedDate) {
        sharedPreferences.edit().putString(KEY_LAST_EXPORT_DATE, localizedDate).apply();
    }
}
