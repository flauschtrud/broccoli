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
    private MutableLiveData<RecipeFilter> filterLiveData = new MutableLiveData<>();

    private CategoryRepository categoryRepository;

    @Inject
    RecipeViewModel(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        filterLiveData.setValue(new RecipeFilter());
        recipes = Transformations.switchMap(filterLiveData, filter -> {
            Category category = filter.getCategory();
            String searchTerm = filter.getSearchTerm();
            if (category == Category.ALL) {
                return "".equals(searchTerm)? recipeRepository.findAll() : recipeRepository.searchFor(searchTerm);
            } else {
                return "".equals(searchTerm)? recipeRepository.filterBy(category) : recipeRepository.filterByAndSearchFor(category, searchTerm);
            }
        });
        this.categoryRepository = categoryRepository;
    }

    LiveData<List<Category>> getCategories() {
        return categoryRepository.findAll();
    }

    LiveData<List<Recipe>> getRecipes() { return recipes; }

    void setFilter(Category category) {
        RecipeFilter newFilter = new RecipeFilter();
        newFilter.setCategory(category);
        newFilter.setSearchTerm(filterLiveData.getValue().getSearchTerm());
        this.filterLiveData.setValue(newFilter);
    }

    void setSearchTerm(String searchTerm) {
        RecipeFilter newFilter = new RecipeFilter();
        newFilter.setCategory(filterLiveData.getValue().getCategory());
        newFilter.setSearchTerm(searchTerm);
        this.filterLiveData.setValue(newFilter);
    }

    private static class RecipeFilter {
        private Category category = Category.ALL;
        private String searchTerm = "";

        Category getCategory() {
            return category;
        }

        void setCategory(Category category) {
            this.category = category;
        }

        String getSearchTerm() {
            return searchTerm;
        }

        void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }
    }

}