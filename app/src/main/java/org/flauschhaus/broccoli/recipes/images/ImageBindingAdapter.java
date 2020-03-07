package org.flauschhaus.broccoli.recipes.images;

import android.graphics.drawable.Drawable;
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

    @BindingAdapter(value = {"imageName", "placeholder"}, requireAll = false)
    public void bind(ImageView imageView, String imageName, Drawable placeholder) {
        if (isEmpty(imageName) && placeholder == null) {
            return;
        }

        if (isEmpty(imageName)) {
            imageView.setImageDrawable(placeholder);
            return;
        }

        Glide.with(imageView)
                .load(recipeImageService.findImage(imageName))
                .centerCrop()
                .into(imageView);

    }

}
