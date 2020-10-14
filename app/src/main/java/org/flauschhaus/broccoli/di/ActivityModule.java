package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.backup.BackupService;
import org.flauschhaus.broccoli.recipe.cooking.CookingModeActivity;
import org.flauschhaus.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import org.flauschhaus.broccoli.recipe.details.RecipeDetailsActivity;

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

    @ContributesAndroidInjector
    BackupService backupService();
}
