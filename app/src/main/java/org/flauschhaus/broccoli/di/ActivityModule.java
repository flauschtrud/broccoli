package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.ui.recipes.CreateAndEditRecipeActivity;
import org.flauschhaus.broccoli.ui.recipes.RecipeDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    CreateAndEditRecipeActivity newRecipeActivity();

    @ContributesAndroidInjector
    RecipeDetailsActivity recipeDetailsActivity();
}
