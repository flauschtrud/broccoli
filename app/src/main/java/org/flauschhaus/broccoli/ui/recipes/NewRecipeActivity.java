package org.flauschhaus.broccoli.ui.recipes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityNewRecipeBinding;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import javax.inject.Inject;

public class NewRecipeActivity extends AppCompatActivity {

    @Inject
    RecipeRepository recipeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((BroccoliApplication) getApplication()).getComponent().inject(this);

        ActivityNewRecipeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_recipe);
        binding.setPresenter(this);
        binding.setRecipe(new Recipe());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        super.onCreate(savedInstanceState);
    }

    public void onSaveClick(Recipe recipe) {
        recipeRepository.insert(recipe);
        finish();
    }
}
