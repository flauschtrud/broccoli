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
    private MutableLiveData<RecipeRepository.SearchCriteria> criteriaLiveData = new MutableLiveData<>();

    private CategoryRepository categoryRepository;

    @Inject
    RecipeViewModel(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        criteriaLiveData.setValue(new RecipeRepository.SearchCriteria());
        recipes = Transformations.switchMap(criteriaLiveData, recipeRepository::find);
        this.categoryRepository = categoryRepository;
    }

    LiveData<List<Category>> getCategories() {
        return categoryRepository.findAll();
    }

    LiveData<List<Recipe>> getRecipes() { return recipes; }

    void setFilter(Category category) {
        RecipeRepository.SearchCriteria newFilter = new RecipeRepository.SearchCriteria();
        newFilter.setCategory(category);
        newFilter.setSearchTerm(criteriaLiveData.getValue().getSearchTerm());
        this.criteriaLiveData.setValue(newFilter);
    }

    void setSearchTerm(String searchTerm) {
        RecipeRepository.SearchCriteria newFilter = new RecipeRepository.SearchCriteria();
        newFilter.setCategory(criteriaLiveData.getValue().getCategory());
        newFilter.setSearchTerm(searchTerm);
        this.criteriaLiveData.setValue(newFilter);
    }

}