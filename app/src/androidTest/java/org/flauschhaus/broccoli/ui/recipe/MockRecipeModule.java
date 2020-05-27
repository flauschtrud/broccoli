package org.flauschhaus.broccoli.ui.recipe;

import org.flauschhaus.broccoli.recipe.RecipeModule;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockRecipeModule extends RecipeModule {

    @Provides
    @Singleton
    RecipeImageService recipeImageService() {
        return mock(RecipeImageService.class);
    }

    @Provides
    @Singleton
    RecipeRepository recipeRepository() {
        return mock(RecipeRepository.class);
    }
}
