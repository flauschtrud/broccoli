package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.category.CategoryFragment;
import org.flauschhaus.broccoli.recipe.list.RecipeFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector()
    RecipeFragment recipesFragment();

    @ContributesAndroidInjector()
    CategoryFragment categoryFragment();

}
