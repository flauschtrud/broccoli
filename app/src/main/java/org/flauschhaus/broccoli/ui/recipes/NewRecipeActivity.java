package org.flauschhaus.broccoli.ui.recipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityNewRecipeBinding;
import org.flauschhaus.broccoli.recipes.Recipe;

public class NewRecipeActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "org.flauschhaus.broccoli.recipes.new.REPLY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNewRecipeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_recipe);
        binding.setPresenter(this);
        binding.setRecipe(new Recipe());
        setSupportActionBar(binding.toolbar);
    }

    public void onSaveClick(Recipe recipe) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, recipe);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

}
