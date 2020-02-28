package org.flauschhaus.broccoli.recipes.images;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

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
        Picasso.get()
                    .load(recipeImageService.getFile(imageName))
                    .error(R.drawable.ic_launcher_foreground)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerCrop()
                    .into(imageView);
    }

}
