package com.flauschcode.broccoli.recipe.cooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.flauschcode.broccoli.recipe.Recipe;

import javax.inject.Inject;

public class CookingModeViewModel extends ViewModel {

    private final PageableRecipeBuilder pageableRecipeBuilder;

    private Recipe recipe;
    private final MutableLiveData<PageableRecipe> pageableRecipe = new MutableLiveData<>();

    @Inject
    public CookingModeViewModel(PageableRecipeBuilder pageableRecipeBuilder) {
        this.pageableRecipeBuilder = pageableRecipeBuilder;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        pageableRecipe.postValue(pageableRecipeBuilder.from(recipe));
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public LiveData<PageableRecipe> getPageableRecipe() {
        return pageableRecipe;
    }

    public void onScale(float scaleFactor) {
        pageableRecipe.postValue(pageableRecipeBuilder.scale(scaleFactor).from(recipe));
    }
}
