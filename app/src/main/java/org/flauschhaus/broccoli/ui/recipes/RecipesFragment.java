package org.flauschhaus.broccoli.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.flauschhaus.broccoli.R;

public class RecipesFragment extends Fragment {

    private RecipesViewModel recipesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recipesViewModel =
                ViewModelProviders.of(this).get(RecipesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        recipesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}