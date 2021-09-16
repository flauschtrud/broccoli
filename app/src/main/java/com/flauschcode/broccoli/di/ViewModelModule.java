package com.flauschcode.broccoli.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.category.CategoryViewModel;
import com.flauschcode.broccoli.recipe.cooking.CookingModeViewModel;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeViewModel;
import com.flauschcode.broccoli.recipe.list.RecipeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface ViewModelModule {

    @Binds
    ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeViewModel.class)
    ViewModel recipeViewModel(RecipeViewModel recipeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel.class)
    ViewModel categoryViewModel(CategoryViewModel categoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateAndEditRecipeViewModel.class)
    ViewModel newRecipeViewModel(CreateAndEditRecipeViewModel createAndEditRecipeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CookingModeViewModel.class)
    ViewModel newCookingModeViewModel(CookingModeViewModel cookingModeViewModel);

}
