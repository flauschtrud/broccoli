package org.flauschhaus.broccoli.ui.recipes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityNewRecipeBinding;
import org.flauschhaus.broccoli.recipes.Recipe;

public class NewRecipeActivity extends AppCompatActivity {

    private NewRecipeViewModel newRecipeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNewRecipeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_recipe);
        binding.setPresenter(this);
        binding.setRecipe(new Recipe());

        newRecipeViewModel = new ViewModelProvider(this).get(NewRecipeViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onSaveClick(Recipe recipe) {
        newRecipeViewModel.insert(recipe);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_new_recipe), Toast.LENGTH_SHORT).show();
        finish();
    }

}
