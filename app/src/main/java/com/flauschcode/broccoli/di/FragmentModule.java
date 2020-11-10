package com.flauschcode.broccoli.di;

import com.flauschcode.broccoli.category.CategoryFragment;
import com.flauschcode.broccoli.recipe.list.RecipeFragment;
import com.flauschcode.broccoli.seasons.SeasonsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector()
    RecipeFragment recipesFragment();

    @ContributesAndroidInjector()
    CategoryFragment categoryFragment();

    @ContributesAndroidInjector
    SeasonsFragment seasonalFragment();
}
