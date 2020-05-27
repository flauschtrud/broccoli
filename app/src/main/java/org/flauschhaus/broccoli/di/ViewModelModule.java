package org.flauschhaus.broccoli.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.flauschhaus.broccoli.category.CategoryViewModel;
import org.flauschhaus.broccoli.recipe.crud.CreateAndEditRecipeViewModel;
import org.flauschhaus.broccoli.recipe.list.RecipeViewModel;

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

}
