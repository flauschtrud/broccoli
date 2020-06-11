package org.flauschhaus.broccoli.recipe.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

public class RecipeViewModel extends ViewModel {

    private LiveData<List<Recipe>> recipes;
    private MutableLiveData<Category> filterLiveData = new MutableLiveData<>();

    private CategoryRepository categoryRepository;

    @Inject
    RecipeViewModel(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        filterLiveData.setValue(Category.ALL);
        recipes = Transformations.switchMap(filterLiveData, category -> {
            if (category == Category.ALL) {
                return recipeRepository.findAll();
            } else {
                return recipeRepository.filterBy(category);
            }
        });
        this.categoryRepository = categoryRepository;
    }

    LiveData<List<Category>> getCategories() {
        return categoryRepository.findAll();
    }

    LiveData<List<Recipe>> getRecipes() { return recipes; }

    void setFilter(Category category) {
        filterLiveData.setValue(category);
    }

    Category getFilter() {
        return filterLiveData.getValue();
    }

}