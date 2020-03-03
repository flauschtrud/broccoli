package org.flauschhaus.broccoli.recipes.images;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import org.flauschhaus.broccoli.R;

import javax.inject.Inject;

public class ImageBindingAdapter {

    private RecipeImageService recipeImageService;

    @Inject
    public ImageBindingAdapter(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    @BindingAdapter("imageName")
    public void bind(ImageView imageView, String imageName) {
        Glide.with(imageView)
                .load(imageName.length() > 0? recipeImageService.getImage(imageName) : R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(imageView);

    }

}
