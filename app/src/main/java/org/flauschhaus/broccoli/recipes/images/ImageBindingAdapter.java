package org.flauschhaus.broccoli.recipes.images;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import static android.text.TextUtils.isEmpty;

public class ImageBindingAdapter {

    private RecipeImageService recipeImageService;

    @Inject
    public ImageBindingAdapter(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    @BindingAdapter("imageName")
    public void bind(ImageView imageView, String imageName) {
        if (isEmpty(imageName)) {
            return;
        }

        Glide.with(imageView)
                .load(recipeImageService.findImage(imageName))
                .centerCrop()
                .into(imageView);

    }

}
