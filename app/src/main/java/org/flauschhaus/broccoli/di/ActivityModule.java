package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.ui.recipes.NewRecipeActivity;
import org.flauschhaus.broccoli.ui.recipes.RecipeDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    NewRecipeActivity newRecipeActivity();

    @ContributesAndroidInjector
    RecipeDetailsActivity recipeDetailsActivity();
}
