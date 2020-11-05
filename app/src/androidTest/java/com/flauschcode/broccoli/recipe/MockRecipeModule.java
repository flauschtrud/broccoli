package com.flauschcode.broccoli.recipe;

import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.RecipeModule;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.cooking.PageableRecipeBuilder;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.recipe.sharing.ShareRecipeAsFileService;
import com.flauschcode.broccoli.recipe.sharing.ShareableRecipeBuilder;

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

    @Provides
    @Singleton
    CategoryRepository categoryRepository() { //TODO move to other mock module
        return mock(CategoryRepository.class);
    }

    @Provides
    @Singleton
    ShareableRecipeBuilder shareableRecipeBuilder() {
        return mock(ShareableRecipeBuilder.class);
    }

    @Provides
    @Singleton
    PageableRecipeBuilder pageableRecipeBuilder() {
        return mock(PageableRecipeBuilder.class);
    }

    @Provides
    @Singleton
    ShareRecipeAsFileService shareRecipeAsFileService() {
        return mock(ShareRecipeAsFileService.class);
    }
}
