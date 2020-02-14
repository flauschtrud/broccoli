package org.flauschhaus.broccoli.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.flauschhaus.broccoli.BroccoliDatabase;
import org.flauschhaus.broccoli.R;

public class SettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        final Button button = root.findViewById(R.id.button_delete_data);
        button.setOnClickListener(view -> BroccoliDatabase.getExecutorService().execute(() -> BroccoliDatabase.get(getContext()).clearAllTables()));

        return root;
    }
}