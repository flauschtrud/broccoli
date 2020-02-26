package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.recipes.images.ImageBindingAdapter;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import dagger.Module;
import dagger.Provides;

@Module
class BindingModule {

    @Provides
    @DataBinding
    ImageBindingAdapter imageBindingAdapter(RecipeImageService recipeImageService) {
        return new ImageBindingAdapter(recipeImageService);
    }

}
