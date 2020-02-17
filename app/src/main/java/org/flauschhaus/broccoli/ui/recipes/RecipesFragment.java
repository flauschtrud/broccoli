package org.flauschhaus.broccoli.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.flauschhaus.broccoli.R;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class RecipesFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        RecipesViewModel recipesViewModel = new ViewModelProvider(this, viewModelFactory).get(RecipesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_recipes, container, false);
        final TextView textView = root.findViewById(R.id.text_recipes);

        recipesViewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> textView.setText(recipes.toString()));

        return root;
    }
}