package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.recipe.list.RecipesFragment;
import org.flauschhaus.broccoli.settings.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector()
    RecipesFragment recipesFragment();

    @ContributesAndroidInjector()
    SettingsFragment settingsFragment();

}
