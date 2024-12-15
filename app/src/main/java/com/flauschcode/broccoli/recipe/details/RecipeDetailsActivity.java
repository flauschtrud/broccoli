package com.flauschcode.broccoli.recipe.details;

import static com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity.DUPLICATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.databinding.ActivityRecipeDetailsBinding;
import com.flauschcode.broccoli.databinding.DirectionItemBinding;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.cooking.CookingAssistantActivity;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import com.flauschcode.broccoli.recipe.directions.DirectionBuilder;
import com.flauschcode.broccoli.recipe.sharing.ShareRecipeAsFileService;
import com.flauschcode.broccoli.recipe.sharing.ShareableRecipe;
import com.flauschcode.broccoli.recipe.sharing.ShareableRecipeBuilder;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.ElevationOverlayProvider;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    ShareRecipeAsFileService shareRecipeAsFileService;

    @Inject
    ShareableRecipeBuilder shareableRecipeBuilder;

    private RecipeDetailsViewModel viewModel;
    private ActivityRecipeDetailsBinding binding;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_details);
        viewModel = new ViewModelProvider(this).get(RecipeDetailsViewModel.class);

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

        binding.fabCookingAssistant.setOnClickListener(view -> this.cook(null));

        // https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed (does not work with expandedTitleTextAppearance because you would see the title fade in nonetheless)
        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbarLayout.setTitle(recipe.getTitle());
                    isShow = true;
                } else if(isShow) {
                    binding.toolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        if (BroccoliApplication.isDarkMode(this)) {
            binding.toolbarLayout.setContentScrimColor(new ElevationOverlayProvider(this).compositeOverlayWithThemeSurfaceColorIfNeeded(8f));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        this.menu = menu;

        MenuItem item = binding.getRecipe().isFavorite()? menu.findItem(R.id.action_details_unlike) : menu.findItem(R.id.action_details_like);
        item.setVisible(true);

        return true;
    }

    ActivityResultLauncher<Intent> editRecipeResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Recipe recipe = (Recipe) result.getData().getSerializableExtra(Recipe.class.getName());
                    binding.setRecipe(recipe);
                }
            });

    public void edit(MenuItem menuItem) {
        Intent intent = new Intent(this, CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), binding.getRecipe());
        editRecipeResultLauncher.launch(intent);
    }

    public void duplicate(MenuItem menuItem) {
        Intent intent = new Intent(this, CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), binding.getRecipe());
        intent.putExtra(DUPLICATE, true);
        editRecipeResultLauncher.launch(intent);
    }

    public void delete(MenuItem menuItem) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.delete_recipe_question)
                .setPositiveButton(R.string.delete_action, (dialog, id) -> {
                    recipeRepository.delete(binding.getRecipe())
                        .thenRun(() -> runOnUiThread(() -> Toast.makeText(this, getString(R.string.recipe_deleted_message), Toast.LENGTH_SHORT).show()));
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {})
                .create();
        alertDialog.show();
    }

    ActivityResultLauncher<Intent> cookingAssistantResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData().getBooleanExtra("navigateToSupportPage", false)) {
                    Intent intent = new Intent();
                    intent.putExtra("navigateToSupportPage", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

    public void cook(MenuItem menuItem) {
        Intent intent = new Intent(getApplicationContext(), CookingAssistantActivity.class);
        intent.putExtra(Recipe.class.getName(), binding.getRecipe());
        cookingAssistantResultLauncher.launch(intent);
    }

    public void toggleFavorite(MenuItem item) {
        binding.getRecipe().setFavorite(!binding.getRecipe().isFavorite());
        recipeRepository.insertOrUpdate(binding.getRecipe()).thenRun(() -> runOnUiThread(() -> {
            item.setVisible(false);
            MenuItem newItem = binding.getRecipe().isFavorite()? menu.findItem(R.id.action_details_unlike) : menu.findItem(R.id.action_details_like);
            newItem.setVisible(true);
        }));
    }

    public void share(MenuItem item) {
        ShareableRecipe shareableRecipe = shareableRecipeBuilder.from(binding.getRecipe());

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, binding.getRecipe().getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareableRecipe.getPlainText());
        if (shareableRecipe.getImageUri() != Uri.EMPTY) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, shareableRecipe.getImageUri());
        }
        shareIntent.setType("text/plain");
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, null));
    }

    ActivityResultLauncher<Intent> shareAsFileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> viewModel.getExportUri().ifPresent(exportUri -> getContentResolver().delete(exportUri, null,  null)));

    public void shareAsFile(MenuItem item) {
        try {
            Uri exportedRecipe = shareRecipeAsFileService.shareAsFile(binding.getRecipe());
            viewModel.setExportUri(exportedRecipe);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, exportedRecipe);
            shareIntent.setType("application/broccoli");
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(shareIntent, null);
            List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, exportedRecipe, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            shareAsFileResultLauncher.launch(chooser);
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
            Toast.makeText(this, getString(R.string.recipe_could_not_be_exported_message), Toast.LENGTH_LONG).show();
        }
    }

    @BindingAdapter("description")
    public static void bindDescription(TextView textView, String description) {
        SpannableString spannableString = new SpannableString(description);

        Pattern pattern = Pattern.compile("#[^\\s!@#$%^&*()=+.\\/,\\[{\\]};:'\"?><]+");
        Matcher matcher = pattern.matcher(description);
        while (matcher.find()) {
            String hashtag = matcher.group();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    Intent intent = new Intent();
                    intent.putExtra("hashtag", hashtag);

                    RecipeDetailsActivity activity = (RecipeDetailsActivity) view.getContext();
                    activity.setResult(RESULT_OK, intent);
                    activity.finish();
                }

                @Override
                public void updateDrawState(TextPaint paint) {
                    super.updateDrawState(paint);
                    paint.setColor(MaterialColors.getColor(textView, com.google.android.material.R.attr.colorPrimary));
                }
            };
            spannableString.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
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

    @BindingAdapter("disableCollapsingScroll")
    public static void bindDisableCollapsingScroll(AppBarLayout appBarLayout, boolean disabled) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();

        if (params.getBehavior() == null) {
            params.setBehavior(new AppBarLayout.Behavior());
        }

        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return disabled;
            }
        });
    }


    private static LayoutInflater getLayoutInflater(LinearLayout layout) {
        return (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
