package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.ui.recipes.NewRecipeActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    NewRecipeActivity newRecipeActivity();

}
