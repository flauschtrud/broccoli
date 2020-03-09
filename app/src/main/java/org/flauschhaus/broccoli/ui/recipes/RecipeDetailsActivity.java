package org.flauschhaus.broccoli.ui.recipes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityRecipeDetailsBinding;
import org.flauschhaus.broccoli.databinding.DirectionItemBinding;
import org.flauschhaus.broccoli.databinding.IngredientItemBinding;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.flauschhaus.broccoli.recipes.ingredients.IngredientBuilder;
import org.flauschhaus.broccoli.recipes.directions.DirectionBuilder;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Inject
    RecipeRepository recipeRepository;

    private ActivityRecipeDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_details);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        binding.setRecipe(recipe);

        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Cook me!", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    public void delete(MenuItem menuItem) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_delete_recipe)
                .setPositiveButton(R.string.action_details_delete, (dialog, id) -> {
                    recipeRepository.delete(binding.getRecipe())
                        .thenRun(() -> runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.toast_recipe_deleted), Toast.LENGTH_SHORT).show()));
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {})
                .create();
        alertDialog.show();
    }

    @BindingAdapter("directions")
    public static void bindDirections(LinearLayout layout, String directions) {
        LayoutInflater inflater = getLayoutInflater(layout);

        DirectionBuilder.from(directions).forEach(direction -> {
            DirectionItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.direction_item, layout, true);
            binding.setDirection(direction);
        });
    }

    @BindingAdapter("ingredients")
    public static void bindIngredients(LinearLayout layout, String ingredients) {
        LayoutInflater inflater = getLayoutInflater(layout);

        IngredientBuilder.from(ingredients).forEach(ingredient -> {
            IngredientItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.ingredient_item, layout, true);
            binding.setIngredient(ingredient);
        });
    }

    private static LayoutInflater getLayoutInflater(LinearLayout layout) {
        return (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
