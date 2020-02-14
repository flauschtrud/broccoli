package org.flauschhaus.broccoli.ui.recipes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

public class NewRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        EditText editTitle = findViewById(R.id.new_recipe_title);
        EditText editDescription = findViewById(R.id.new_recipe_description);

        final Button button = findViewById(R.id.button_recipe_save);
        button.setOnClickListener(view -> {
            RecipeRepository recipeRepository = new RecipeRepository(getApplication());
            Recipe recipe = new Recipe();
            recipe.setTitle(editTitle.getText().toString());
            recipe.setDescription(editDescription.getText().toString());
            recipeRepository.insert(recipe);
            finish();
        });
    }
}
