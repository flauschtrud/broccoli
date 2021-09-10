package com.flauschcode.broccoli.recipe.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.flauschcode.broccoli.FeatureDiscoveryTargetBuilder;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CookingModeActivity extends AppCompatActivity implements CookingModeControls.OnCookingModeControlsInteractionListener  {

    @Inject
    PageableRecipeBuilder pageableRecipeBuilder;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_cooking_mode);

        viewPager = findViewById(R.id.cooking_mode_pager);

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view -> finish());

        Button scalingButton = findViewById(R.id.button_scaling);
        scalingButton.setOnClickListener(view -> showScalingDialog());

        FeatureDiscoveryTargetBuilder.buildInContextOf(this)
                .withTitle(getString(R.string.scale_the_ingredients))
                .withDescription(getString(R.string.scaling_question))
                .discoverIfNew(scalingButton);

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
                .setMessage(R.string.scaling_question)
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    EditText inputScaleFactor = view.findViewById(R.id.input_scale_factor);
                    if (!TextUtils.isEmpty(inputScaleFactor.getText())) {
                        createPageableRecipe(Float.parseFloat(inputScaleFactor.getText().toString()));
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {})
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

    private void hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (insetsController != null) {
            insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            insetsController.hide(WindowInsetsCompat.Type.statusBars());
            insetsController.hide(WindowInsetsCompat.Type.navigationBars());
        }
    }

    @Override
    public void onCookingModeControlsInteraction(int position) {
        viewPager.setCurrentItem(position);
    }

    public void navigateToSupportPage(View view) {
        Intent intent = new Intent();
        intent.putExtra("navigateToSupportPage", true);
        setResult(RESULT_OK, intent);
        finish();
    }

}
