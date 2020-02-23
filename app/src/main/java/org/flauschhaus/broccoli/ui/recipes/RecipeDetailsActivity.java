package org.flauschhaus.broccoli.ui.recipes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityRecipeDetailsBinding;
import org.flauschhaus.broccoli.databinding.IngredientItemBinding;
import org.flauschhaus.broccoli.databinding.InstructionItemBinding;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.ingredients.IngredientBuilder;
import org.flauschhaus.broccoli.recipes.instructions.InstructionBuilder;

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

    @BindingAdapter("instructions")
    public static void bindInstructions(LinearLayout layout, String instructions) {
        LayoutInflater inflater = getLayoutInflater(layout);

        InstructionBuilder.from(instructions).forEach(instruction -> {
            InstructionItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.instruction_item, layout, true);
            binding.setInstruction(instruction);
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
