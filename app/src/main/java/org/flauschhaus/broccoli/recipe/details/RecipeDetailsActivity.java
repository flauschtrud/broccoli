package org.flauschhaus.broccoli.recipe.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.databinding.ActivityRecipeDetailsBinding;
import org.flauschhaus.broccoli.databinding.DirectionItemBinding;
import org.flauschhaus.broccoli.databinding.IngredientItemBinding;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.flauschhaus.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import org.flauschhaus.broccoli.recipe.directions.DirectionBuilder;
import org.flauschhaus.broccoli.recipe.ingredients.IngredientBuilder;
import org.flauschhaus.broccoli.recipe.sharing.RecipeSharingService;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    RecipeSharingService recipeSharingService;

    private ActivityRecipeDetailsBinding binding;
    private Menu menu;

    private static final int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_details);

        NestedScrollView nestedScrollView = findViewById(R.id.scroll_view);
        nestedScrollView.post(() -> {
            int appBarHeight = binding.appBar.getHeight()/2;
            nestedScrollView.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
            nestedScrollView.dispatchNestedPreScroll(0, appBarHeight, null, null);
            nestedScrollView.dispatchNestedScroll(0, 0, 0, 0, new int[]{0, -appBarHeight});
        });

        binding.backdrop.setOnClickListener(view -> binding.appBar.setExpanded(true, true));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        binding.setRecipe(recipe);

        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Cook me!", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);

        MenuItem item = binding.getRecipe().isFavorite()? menu.findItem(R.id.action_details_unlike) : menu.findItem(R.id.action_details_like);
        item.setVisible(true);

        this.menu = menu;

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            Recipe recipe = (Recipe) data.getSerializableExtra(Recipe.class.getName());
            binding.setRecipe(recipe);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void edit(MenuItem menuItem) {
        Intent intent = new Intent(this, CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), binding.getRecipe());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    public void delete(MenuItem menuItem) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_delete_recipe)
                .setPositiveButton(R.string.action_delete, (dialog, id) -> {
                    recipeRepository.delete(binding.getRecipe())
                        .thenRun(() -> runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.toast_recipe_deleted), Toast.LENGTH_SHORT).show()));
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {})
                .create();
        alertDialog.show();
    }

    public void toggleFavorite(MenuItem item) {
        binding.getRecipe().setFavorite(!binding.getRecipe().isFavorite());
        recipeRepository.insertOrUpdate(binding.getRecipe()).thenRun(() -> runOnUiThread(() -> {
            item.setVisible(false);
            MenuItem newItem = binding.getRecipe().isFavorite()? menu.findItem(R.id.action_details_unlike) : menu.findItem(R.id.action_details_like);
            newItem.setVisible(true);
        }));
    }

    public void share(MenuItem item) { // TODO test
        String html = recipeSharingService.toHtml(binding.getRecipe());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        sendIntent.setType("text/html");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @BindingAdapter("categories")
    public static void bindCategories(TextView textView, List<Category> categories) {
        String joinedCategories = categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
        textView.setText(joinedCategories);
    }

    @BindingAdapter("directions")
    public static void bindDirections(LinearLayout layout, String directions) {
        layout.removeAllViews();

        LayoutInflater inflater = getLayoutInflater(layout);

        DirectionBuilder.from(directions).forEach(direction -> {
            DirectionItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.direction_item, layout, true);
            binding.setDirection(direction);
        });
    }

    @BindingAdapter("ingredients")
    public static void bindIngredients(LinearLayout layout, String ingredients) {
        layout.removeAllViews();

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
