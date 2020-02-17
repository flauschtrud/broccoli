package org.flauschhaus.broccoli.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SettingsFragment extends Fragment {

    @Inject
    RecipeRepository recipeRepository;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        final Button button = root.findViewById(R.id.button_delete_data);
        button.setOnClickListener(view -> recipeRepository.deleteAll());

        return root;
    }
}