package org.flauschhaus.broccoli.ui.recipes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.ActivityNewRecipeBinding;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class NewRecipeActivity extends AppCompatActivity {

    @Inject
    RecipeImageService recipeImageService;

    public static final String EXTRA_REPLY = "org.flauschhaus.broccoli.recipes.new.REPLY";

    private static final int REQUEST_TAKE_PHOTO = 1;

    private ActivityNewRecipeBinding binding;
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_recipe);
        binding.setPresenter(this);
        binding.setRecipe(new Recipe());
        setSupportActionBar(binding.toolbar);
    }

    public void onSaveClick(Recipe recipe) {
        if (recipe.getTitle().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_title_is_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, recipe);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void dispatchImageCaptureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = recipeImageService.createImageFile();
                imageName = photoFile.getName();
                Uri photoURI = FileProvider.getUriForFile(this,"org.flauschhaus.broccoli.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {
                Toast.makeText(this, getString(R.string.toast_error_creating_image_file), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            binding.getRecipe().setImageName(imageName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
