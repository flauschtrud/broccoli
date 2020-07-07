package org.flauschhaus.broccoli.recipe.cooking;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CookingModeActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    @Inject
    PageableRecipeBuilder pageableRecipeBuilder;

    private ViewPager2 viewPager;

    private boolean immersive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_cooking_mode);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        PageableRecipe pageableRecipe = pageableRecipeBuilder.from(recipe);

        CookingModeAdapter adapter = new CookingModeAdapter(this);
        adapter.setPageableRecipe(pageableRecipe != null? pageableRecipe : new PageableRecipe());

        viewPager = findViewById(R.id.cooking_mode_pager);
        viewPager.setAdapter(adapter);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        viewPager.setCurrentItem(progress, false);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // intentionally empty
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // intentionally empty
    }
}
