package com.flauschcode.broccoli.di;

import com.flauschcode.broccoli.recipe.cooking.CookingModeActivity;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import com.flauschcode.broccoli.recipe.details.RecipeDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    CreateAndEditRecipeActivity createAndEditRecipeActivity();

    @ContributesAndroidInjector
    RecipeDetailsActivity recipeDetailsActivity();

    @ContributesAndroidInjector
    CookingModeActivity cookingModeActivity();

}
