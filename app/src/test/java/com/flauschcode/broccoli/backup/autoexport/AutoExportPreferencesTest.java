package com.flauschcode.broccoli.backup.autoexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AutoExportPreferencesTest {

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SharedPreferences.Editor editor;

    private AutoExportPreferences autoExportPreferences;

    @Before
    public void setUp() {
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor);
        when(editor.putString(anyString(), any())).thenReturn(editor);

        autoExportPreferences = new AutoExportPreferences(sharedPreferences);
    }

    @Test
    public void disabled_by_default() {
        when(sharedPreferences.getBoolean("auto-export-enabled", false)).thenReturn(false);

        assertThat(autoExportPreferences.isEnabled(), is(false));
    }

    @Test
    public void enabling_persists_the_flag() {
        autoExportPreferences.setEnabled(true);

        verify(editor).putBoolean("auto-export-enabled", true);
        verify(editor).apply();
    }

    @Test
    public void no_directory_chosen_by_default() {
        when(sharedPreferences.getString("auto-export-directory-uri", null)).thenReturn(null);

        assertThat(autoExportPreferences.getExportDirectoryUri(), is(Optional.empty()));
    }

    @Test
    public void returns_the_chosen_directory() {
        when(sharedPreferences.getString("auto-export-directory-uri", null)).thenReturn("content://tree/123");

        assertThat(autoExportPreferences.getExportDirectoryUri(), is(Optional.of("content://tree/123")));
    }

    @Test
    public void choosing_a_directory_persists_it() {
        autoExportPreferences.setExportDirectoryUri("content://tree/123");

        verify(editor).putString("auto-export-directory-uri", "content://tree/123");
        verify(editor).apply();
    }

    @Test
    public void recording_the_last_export_date_persists_it() {
        autoExportPreferences.saveLastExportDate("01/02/2026");

        verify(editor).putString("last-auto-export-date", "01/02/2026");
        verify(editor).apply();
    }
}
