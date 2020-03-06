package org.flauschhaus.broccoli.ui.recipes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityNewRecipeBinding;
import org.flauschhaus.broccoli.recipes.Recipe;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class NewRecipeActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private NewRecipeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(NewRecipeViewModel.class);
        if (savedInstanceState != null) {
            viewModel.setRecipe((Recipe) savedInstanceState.getSerializable("recipe"));
            viewModel.setImageName(savedInstanceState.getString("imageName"));
        }

        ActivityNewRecipeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_recipe);
        binding.setPresenter(this);
        binding.setViewModel(viewModel);

        setSupportActionBar(binding.toolbar);
    }

    public void onSaveClick(Recipe recipe) {
        if (recipe.getTitle().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_title_is_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            viewModel.save();
            Toast.makeText(this, getString(R.string.toast_new_recipe), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.toast_error_saving_image_file), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    public void onImageClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                Uri imageUri = viewModel.createAndRememberImage();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException ex) {
                Toast.makeText(this, getString(R.string.toast_error_creating_image_file), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            viewModel.applyImageToRecipe();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("recipe", viewModel.getRecipe());
        outState.putString("imageName", viewModel.getImageName());
        super.onSaveInstanceState(outState);
    }

}
