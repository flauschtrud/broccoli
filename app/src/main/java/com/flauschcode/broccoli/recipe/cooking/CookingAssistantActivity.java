package com.flauschcode.broccoli.recipe.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CookingAssistantActivity extends AppCompatActivity implements CookingAssistantControls.OnCookingAssistantControlsInteractionListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_cooking_assistant);

        viewPager = findViewById(R.id.cooking_assistant_pager);

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view -> finish());

        Button scalingButton = findViewById(R.id.button_scaling);
        scalingButton.setOnClickListener(view -> showScalingDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fullscreen_layout), (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            ViewGroup.MarginLayoutParams cancelParams = (ViewGroup.MarginLayoutParams) cancelButton.getLayoutParams();
            cancelParams.topMargin = statusBarInsets.top;
            cancelButton.setLayoutParams(cancelParams);

            ViewGroup.MarginLayoutParams scalingParams = (ViewGroup.MarginLayoutParams) scalingButton.getLayoutParams();
            scalingParams.topMargin = statusBarInsets.top;
            scalingButton.setLayoutParams(scalingParams);
            return insets;
        });

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());

        CookingAssistantViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(CookingAssistantViewModel.class);
        viewModel.setRecipe(recipe);
        viewModel.getPageableRecipe().observe(this, this::setPageableRecipe);
    }

    private void setPageableRecipe(PageableRecipe pageableRecipe) {
        CookingAssistantAdapter adapter = new CookingAssistantAdapter(this);
        adapter.setPageableRecipe(pageableRecipe != null? pageableRecipe : new PageableRecipe());

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCookingAssistantControlsInteraction(int position) {
        viewPager.setCurrentItem(position);
    }

    public void navigateToSupportPage(View view) {
        Intent intent = new Intent();
        intent.putExtra("navigateToSupportPage", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showScalingDialog() {
        ScalingDialog scalingDialog = new ScalingDialog();
        scalingDialog.show(getSupportFragmentManager(), "ScalingDialogFragment");
    }
}
