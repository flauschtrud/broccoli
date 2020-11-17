package com.flauschcode.broccoli.recipe.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.MainActivity;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.RecyclerViewAdapter;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import com.flauschcode.broccoli.recipe.details.RecipeDetailsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static android.app.Activity.RESULT_OK;

public class RecipeFragment extends Fragment implements AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RecipeViewModel viewModel;

    private MenuItem searchItem;
    private SearchView searchView;

    private static final int REQUEST_CRUD = 1;
    private static final int REQUEST_DETAILS = 2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        ListAdapter<Recipe, RecyclerViewAdapter<Recipe>.Holder> adapter = new RecyclerViewAdapter<Recipe>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.recipe_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.recipe;
            }

            @Override
            protected void onItemClick(Recipe item) {
                onListInteraction(item);
            }
        };
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab_recipes);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CreateAndEditRecipeActivity.class);
            startActivityForResult(intent, REQUEST_CRUD);
        });

        viewModel = new ViewModelProvider(this, viewModelFactory).get(RecipeViewModel.class);
        viewModel.getRecipes().observe(getViewLifecycleOwner(), adapter::submitList);

        Spinner spinner = getActivity().findViewById(R.id.spinner);
        if (spinner != null) {
            spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
            arrayAdapter.add(Category.ALL);

            if (seasonalCalendarIsEnabled()) {
                arrayAdapter.add(Category.SEASONAL);
            }

            arrayAdapter.add(Category.UNASSIGNED);
            arrayAdapter.add(Category.FAVORITES);
            viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> categories.forEach(arrayAdapter::add));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(this);

            Category preferredCategory = getPreferredCategory();
            int position = arrayAdapter.getPosition(preferredCategory);
            spinner.setSelection(position);
        }

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.recipes, menu);
        searchItem = menu.findItem(R.id.action_search);

        if (getActivity() instanceof MainActivity) {
            searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
            searchItem.setActionView(searchView);
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Spinner spinner = getActivity().findViewById(R.id.spinner);
        if (spinner != null) {
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getItemAtPosition(position);
        viewModel.setFilter(category);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRUD && resultCode == RESULT_OK) {
            Recipe recipe = (Recipe) data.getSerializableExtra(Recipe.class.getName());
            resetCategory();
            if (getActivity() instanceof MainActivity) {
                searchItem.expandActionView();
                searchView.post(() -> searchView.setQuery(recipe.getTitle(), false));
            }
        } else if (requestCode == REQUEST_DETAILS && resultCode == RESULT_OK && data.hasExtra("hashtag")) {
            resetCategory();
            searchItem.expandActionView();
            searchView.post(() -> searchView.setQuery(data.getStringExtra("hashtag"), false));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // intentionally empty
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        viewModel.setSearchTerm(newText);
        return false;
    }

    private void onListInteraction(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
        intent.putExtra(Recipe.class.getName(), recipe);
        startActivityForResult(intent, REQUEST_DETAILS);
    }

    private void resetCategory() {
        Spinner spinner = getActivity().findViewById(R.id.spinner);
        if (spinner != null) {
            spinner.setSelection(0);
            viewModel.setFilter(Category.ALL);
        }
    }

    private boolean seasonalCalendarIsEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getBoolean("seasonal-calendar-enabled", false);
    }

    private Category getPreferredCategory() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String preferredCategoryId = sharedPreferences.getString("preferred-category", "-1");
        switch (preferredCategoryId)  {
            case "-2":
                return Category.FAVORITES;
            case "-4":
                return Category.SEASONAL;
            default:
                return Category.ALL;
        }
    }

}