package com.flauschcode.broccoli.recipe.crud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.BooleanUtils;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.recipe.importing.RecipeImportService;
import com.flauschcode.broccoli.recipe.sharing.ShareRecipeAsFileService;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.databinding.ActivityNewRecipeBinding;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CreateAndEditRecipeActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    RecipeImportService recipeImportService;

    @Inject
    ShareRecipeAsFileService shareRecipeAsFileService;

    private CreateAndEditRecipeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(CreateAndEditRecipeViewModel.class);
        ActivityNewRecipeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_recipe);

        Intent intent = getIntent();

        if (intent.hasExtra(Recipe.class.getName())) {
            Recipe recipe = (Recipe) intent.getSerializableExtra(Recipe.class.getName());
            viewModel.setRecipe(recipe);
        }

        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            String url = parseUrlFrom(intent);
            recipeImportService.importFrom(url)
                    .thenAccept(optionalRecipe -> {
                        if (optionalRecipe.isPresent()) {
                            applyRecipeToViewModel(optionalRecipe.get());
                            binding.setViewModel(viewModel);
                        } else {
                            runOnUiThread(() -> Toast.makeText(this, getString(R.string.toast_error_reading_recipe), Toast.LENGTH_LONG).show());
                        }
                    })
                    .exceptionally(e -> {
                        Log.e(getClass().getName(), e.getMessage());
                        runOnUiThread(() -> Toast.makeText(this, getString(R.string.toast_error_importing_recipe), Toast.LENGTH_SHORT).show());
                        return null;
                    });
        }

        if (Intent.ACTION_DEFAULT.equals(intent.getAction())) {
            Uri uri = getIntent().getData();
            Optional<Recipe> optionalRecipe = shareRecipeAsFileService.loadFromFile(uri);
            if (optionalRecipe.isPresent()) {
                applyRecipeToViewModel(optionalRecipe.get());
            } else {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.toast_error_importing_recipe), Toast.LENGTH_SHORT).show());
            }

        }

        if (savedInstanceState != null) {
            viewModel.setRecipe((Recipe) savedInstanceState.getSerializable("recipe"));
            viewModel.setNewImageName(savedInstanceState.getString("newImageName"));
            viewModel.setOldImageName(savedInstanceState.getString("oldImageName"));
        }

        binding.setActivity(this);
        binding.setViewModel(viewModel);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> showDiscardDialog(this::finish));
    }

    @Override
    public void onBackPressed() {
        showDiscardDialog(super::onBackPressed);
    }

    private void showDiscardDialog(Runnable onDiscard) {
        if (!viewModel.getRecipe().isDirty()) {
            onDiscard.run();
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_discard_changes)
                .setPositiveButton(R.string.action_discard, (dialog, id) -> onDiscard.run())
                .setNegativeButton(R.string.cancel, (dialog, id) -> {})
                .create();
        alertDialog.show();
    }

    public void onSaveClick(Recipe recipe) {
        if (recipe.getTitle().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_title_is_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.confirmFinishedBySaving();
        viewModel.save()
                .thenAccept(result -> {
                    if (result == RecipeRepository.InsertionType.INSERT) {
                        runOnUiThread(() -> Toast.makeText(this, getString(R.string.toast_new_recipe), Toast.LENGTH_SHORT).show());
                    } else if (result == RecipeRepository.InsertionType.UPDATE){
                        runOnUiThread(() -> Toast.makeText(this, getString(R.string.toast_recipe_updated), Toast.LENGTH_SHORT).show());
                    }
                })
                .exceptionally(e -> {
                    runOnUiThread(() -> Toast.makeText(this, getString(R.string.toast_error_saving_recipe), Toast.LENGTH_SHORT).show());
                    return null;
                });

        Intent intent = new Intent();
        intent.putExtra(Recipe.class.getName(), recipe);
        setResult(RESULT_OK, intent);

        finish();
    }

    public void onImageClick() {
        ArrayMap<CharSequence, Runnable> items = new ArrayMap<>();
        if (viewModel.imageHasBeenSet()) {
            items.put(getString(R.string.remove_photo), viewModel::confirmImageHasBeenRemoved);
        }
        items.put(getString(R.string.take_photo), this::takePicture);
        items.put(getString(R.string.pick_photo), this::pickPicture);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.change_image)
                .setItems(items.keySet().toArray(new CharSequence[0]), (dialog, which) -> items.valueAt(which).run());
        alertDialog.show();
    }

    ActivityResultLauncher<Uri> takePictureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (Boolean.TRUE.equals(result)) {
                    viewModel.confirmImageHasBeenTaken();
                }
            });

    private void takePicture() {
        try {
            Uri imageUri = viewModel.createAndRememberImage();
            takePictureResultLauncher.launch(imageUri);
        } catch (IOException ex) {
            Toast.makeText(this, getString(R.string.toast_error_creating_image_file), Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<String> getContentResultLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> viewModel.confirmImageHasBeenPicked(uri));

    private void pickPicture() {
        getContentResultLauncher.launch("image/*");
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (start == 0 && before == 0) { // call triggered by data binding
            return; // TODO does also return when a single character is typed https://github.com/JanaFlauschata/broccoli/issues/20
        }

        viewModel.getRecipe().setDirty(true);
    }

    public void onCategoryClick(View view) {
        viewModel.getCategories().observe(this, categories -> {

            CharSequence[] categoryNames = categories.stream().map(Category::getName).toArray(CharSequence[]::new);
            boolean[] checkedCategories = categories.stream().map(category -> viewModel.getRecipe().getCategories().contains(category)).collect(BooleanUtils.toBooleanArray);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                    .setTitle(R.string.categories)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {});

            if (categoryNames.length == 0) {
                dialogBuilder.setMessage(R.string.no_categories);
            } else {
                dialogBuilder.setMultiChoiceItems(categoryNames, checkedCategories, (dialog, which, isChecked) -> {
                    Category category = categories.get(which);
                    viewModel.getRecipe().setDirty(true);
                    if (isChecked) {
                        viewModel.getRecipe().addCategory(category);
                    }
                    else {
                        viewModel.getRecipe().removeCategory(category);
                    }
                });
            }

            dialogBuilder.create()
                    .show();

        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("recipe", viewModel.getRecipe());
        outState.putString("newImageName", viewModel.getNewImageName());
        outState.putString("oldImageName", viewModel.getOldImageName());
        super.onSaveInstanceState(outState);
    }

    private void applyRecipeToViewModel(Recipe recipe) {
        viewModel.setRecipe(recipe);
        if (!"".equals(recipe.getImageName())) {
            viewModel.setNewImageName(recipe.getImageName());
        }
    }

    private String parseUrlFrom(Intent intent) {
        String extra = intent.getStringExtra(Intent.EXTRA_TEXT);
        Matcher matcher = Patterns.WEB_URL.matcher(extra);
        if (matcher.find()) {
            return matcher.group();
        }
        Log.e(getClass().getName(), "Could not extract valid URL from :'" + extra + "'.");
        return extra;
    }

}
