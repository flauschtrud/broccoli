package org.flauschhaus.broccoli.recipe.images;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import org.flauschhaus.broccoli.R;

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
            Glide.with(imageView)
                    .clear(imageView);
            return;
        }

        if (isEmpty(imageName)) {
            imageView.setImageDrawable(placeholder);
            return;
        }

        Glide.with(imageView)
                .load(recipeImageService.findImage(imageName))
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(imageView);

    }

}
