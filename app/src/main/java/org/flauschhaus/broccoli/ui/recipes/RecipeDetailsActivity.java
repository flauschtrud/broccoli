package org.flauschhaus.broccoli.ui.recipes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityRecipeDetailsBinding;
import org.flauschhaus.broccoli.recipes.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRecipeDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_details);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        binding.setRecipe(recipe);

        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Cook me!", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}
