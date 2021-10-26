package com.flauschcode.broccoli.di;

import com.flauschcode.broccoli.MainActivity;
import com.flauschcode.broccoli.recipe.cooking.CookingAssistantActivity;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import com.flauschcode.broccoli.recipe.details.RecipeDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    MainActivity mainActivity();

    @ContributesAndroidInjector
    CreateAndEditRecipeActivity createAndEditRecipeActivity();

    @ContributesAndroidInjector
    RecipeDetailsActivity recipeDetailsActivity();

    @ContributesAndroidInjector
    CookingAssistantActivity cookingAssistantActivity();

}
