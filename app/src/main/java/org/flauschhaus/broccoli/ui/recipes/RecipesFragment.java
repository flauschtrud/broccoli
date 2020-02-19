package org.flauschhaus.broccoli.ui.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static android.app.Activity.RESULT_OK;

public class RecipesFragment extends Fragment implements RecipeAdapter.OnListFragmentInteractionListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    static final int NEW_RECIPE_ACTIVITY_REQUEST_CODE = 1;

    private RecipesViewModel recipesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        recipesViewModel = new ViewModelProvider(this, viewModelFactory).get(RecipesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        RecipeAdapter adapter = new RecipeAdapter();
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), NewRecipeActivity.class);
            startActivityForResult(intent, NEW_RECIPE_ACTIVITY_REQUEST_CODE);
        });

        recipesViewModel.getRecipes().observe(getViewLifecycleOwner(), adapter::submitList);

        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_RECIPE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Recipe recipe = (Recipe) data.getSerializableExtra(NewRecipeActivity.EXTRA_REPLY);
            recipesViewModel.insert(recipe);
            Toast.makeText(getActivity(), getString(R.string.toast_new_recipe), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListFragmentInteraction(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
        intent.putExtra(Recipe.class.getName(), recipe);
        startActivity(intent);
    }
}