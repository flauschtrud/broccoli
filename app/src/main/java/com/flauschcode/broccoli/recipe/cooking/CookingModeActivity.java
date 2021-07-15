package com.flauschcode.broccoli.recipe.cooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CookingModeActivity extends AppCompatActivity implements CookingModeControls.OnCookingModeControlsInteractionListener  {

    @Inject
    PageableRecipeBuilder pageableRecipeBuilder;

    private ViewPager2 viewPager;

    private boolean immersive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_cooking_mode);

        viewPager = findViewById(R.id.cooking_mode_pager);

        findViewById(R.id.button_scaling).setOnClickListener(view -> showScalingDialog());

        createPageableRecipe();
    }

    private void createPageableRecipe(float scaleFactor) {
        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        PageableRecipe pageableRecipe = pageableRecipeBuilder.scale(scaleFactor).from(recipe);

        setPageableRecipe(pageableRecipe);
    }

    private void createPageableRecipe() {
        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        PageableRecipe pageableRecipe = pageableRecipeBuilder.from(recipe);

        setPageableRecipe(pageableRecipe);
    }

    private void setPageableRecipe(PageableRecipe pageableRecipe) {
        CookingModeAdapter adapter = new CookingModeAdapter(this);
        adapter.setPageableRecipe(pageableRecipe != null? pageableRecipe : new PageableRecipe());

        viewPager.setAdapter(adapter);
    }

    private void showScalingDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_scaling, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.scale_the_ingredients)
                .setMessage(R.string.scaling_message)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    EditText inputScaleFactor = view.findViewById(R.id.input_scale_factor);
                    if (!TextUtils.isEmpty(inputScaleFactor.getText())) {
                        createPageableRecipe(Float.parseFloat(inputScaleFactor.getText().toString()));
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {})
                .create();
        alertDialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    public void toggleSystemUI(View view) {
        if (immersive) {
            showSystemUI();
        } else {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        immersive = true;
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        immersive = false;
    }

    @Override
    public void onCookingModeControlsInteraction(int position) {
        viewPager.setCurrentItem(position);
    }

}
