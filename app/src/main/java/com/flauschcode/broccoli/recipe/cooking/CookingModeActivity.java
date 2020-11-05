package com.flauschcode.broccoli.recipe.cooking;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING;

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

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        PageableRecipe pageableRecipe = pageableRecipeBuilder.from(recipe);

        CookingModeAdapter adapter = new CookingModeAdapter(this);
        adapter.setPageableRecipe(pageableRecipe != null? pageableRecipe : new PageableRecipe());

        viewPager = findViewById(R.id.cooking_mode_pager);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            private boolean settled = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == SCROLL_STATE_DRAGGING) {
                    settled = false;
                }
                if (state == SCROLL_STATE_SETTLING) {
                    settled = true;
                }
                if (state == SCROLL_STATE_IDLE && !settled) {
                    finish();
                }
            }
        });
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
