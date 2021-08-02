package com.flauschcode.broccoli.recipe.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecipeViewModel extends ViewModel {

    private final LiveData<List<Recipe>> recipes;
    private final MutableLiveData<RecipeRepository.SearchCriteria> criteriaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> filterName = new MutableLiveData<>();

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;

    @Inject
    RecipeViewModel(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;

        criteriaLiveData.setValue(createDefaultSearchCriteria());
        filterName.setValue("");

        recipes = Transformations.switchMap(criteriaLiveData, recipeRepository::find);
    }

    LiveData<List<Category>> getCategories() {
        return categoryRepository.findAll();
    }

    LiveData<List<Recipe>> getRecipes() { return recipes; }

    void setFilterCategory(Category filterCategory) {
        RecipeRepository.SearchCriteria searchCriteria = criteriaLiveData.getValue();
        searchCriteria.setCategory(filterCategory);

        this.criteriaLiveData.setValue(searchCriteria);
    }

    void setSearchTerm(String searchTerm) {
        RecipeRepository.SearchCriteria searchCriteria = criteriaLiveData.getValue();
        searchCriteria.setSearchTerm(searchTerm);

        this.criteriaLiveData.setValue(searchCriteria);
    }

    void setSeasonalTerms(List<String> seasonalTerms) {
        RecipeRepository.SearchCriteria searchCriteria = createDefaultSearchCriteria();
        searchCriteria.setSeasonalTerms(seasonalTerms);

        this.criteriaLiveData.setValue(searchCriteria);
    }

    public MutableLiveData<String> getFilterName() {
        return filterName;
    }

    void setFilterName(String filterName) {
        this.filterName.setValue(filterName);
    }

    public Category getCategoryAll() {
        return recipeRepository.getCategoryAll();
    }

    public Category getCategoryFavorites() {
        return recipeRepository.getCategoryFavorites();
    }

    public Category getCategoryUnassigned() {
        return recipeRepository.getCategoryUnassigned();
    }

    public Category getCategorySeasonal() {
        return recipeRepository.getCategorySeasonal();
    }

    private RecipeRepository.SearchCriteria createDefaultSearchCriteria() {
        return new RecipeRepository.SearchCriteria(recipeRepository.getCategoryAll(), "", new ArrayList<>());
    }

}