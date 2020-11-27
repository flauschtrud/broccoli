package com.flauschcode.broccoli.recipe;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.seasons.SeasonalCalendar;
import com.flauschcode.broccoli.seasons.SeasonalCalendarHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeRepository {

    private RecipeDAO recipeDAO;
    private RecipeImageService recipeImageService;
    private SeasonalCalendarHolder seasonalCalendarHolder;

    public enum InsertionType {
        INSERT, UPDATE
    }

    @Inject
    RecipeRepository(RecipeDAO recipeDAO, RecipeImageService recipeImageService, SeasonalCalendarHolder seasonalCalendarHolder) {
        this.recipeDAO = recipeDAO;
        this.recipeImageService = recipeImageService;
        this.seasonalCalendarHolder = seasonalCalendarHolder;
    }

    public LiveData<List<Recipe>> find(SearchCriteria criteria) {
        Category category = criteria.getCategory();
        String searchTerm = criteria.getSearchTerm();

        if (!"".equals(criteria.getSeasonalTerm())) {
            String wildcardQuery = String.format("%s*", searchTerm);
            return "".equals(searchTerm)? recipeDAO.findSeasonal(criteria.getSeasonalTerm()) : recipeDAO.searchForSeasonal(criteria.getSeasonalTerm(), wildcardQuery);
        }

        if (category == Category.ALL || category == Category.FAVORITES) {
            List<Boolean> favoriteStates = getChosenFavoriteStates(category);
            return "".equals(searchTerm)? findAll(favoriteStates) : searchFor(searchTerm, favoriteStates);
        }

        if (category == Category.UNASSIGNED) {
            return "".equals(searchTerm)? findUnassigned() : searchForUnassigned(searchTerm);
        }

        if (category == Category.SEASONAL) {
            return "".equals(searchTerm)? findSeasonal() : searchForSeasonal(searchTerm);
        }

        return "".equals(searchTerm)? filterBy(category) : filterByAndSearchFor(category, searchTerm);
    }

    private LiveData<List<Recipe>> findAll(List<Boolean> favoritesList) {
        return recipeDAO.findAll(favoritesList);
    }

    private LiveData<List<Recipe>> filterBy(Category category) {
        return recipeDAO.filterBy(category.getCategoryId());
    }

    private LiveData<List<Recipe>> searchFor(String term, List<Boolean> favoritesList) {
        String wildcardQuery = String.format("%s*", term);
        return recipeDAO.searchFor(wildcardQuery, favoritesList);
    }

    private LiveData<List<Recipe>> filterByAndSearchFor(Category category, String term) {
        String wildcardQuery = String.format("%s*", term);
        return recipeDAO.filterByAndSearchFor(category.getCategoryId(), wildcardQuery);
    }

    private LiveData<List<Recipe>> findUnassigned() {
        return recipeDAO.findUnassigned();
    }

    private LiveData<List<Recipe>> searchForUnassigned(String term) {
        String wildcardQuery = String.format("%s*", term);
        return recipeDAO.searchForUnassigned(wildcardQuery);
    }

    private LiveData<List<Recipe>> findSeasonal() {
        String seasonalSearchTerm = getSeasonalSearchTerm();
        return recipeDAO.findSeasonal(seasonalSearchTerm);
    }

    private LiveData<List<Recipe>> searchForSeasonal(String term) {
        String wildcardQuery = String.format("%s*", term);
        String seasonalSearchTerm = getSeasonalSearchTerm();
        return recipeDAO.searchForSeasonal(seasonalSearchTerm, wildcardQuery);
    }

    private String getSeasonalSearchTerm() {
        Optional<SeasonalCalendar> seasonalCalendarOptional = seasonalCalendarHolder.get();
        if (seasonalCalendarOptional.isPresent()) {
            SeasonalCalendar seasonalCalendar = seasonalCalendarOptional.get();
            return seasonalCalendar.getSearchTermsForCurrentMonth().stream().map(term -> "\"" + term + "\"").collect(Collectors.joining(" OR "));
        }
        return "";
    }

    private List<Boolean> getChosenFavoriteStates(Category category) {
        List<Boolean> favoriteStates = new ArrayList<>();
        favoriteStates.add(Boolean.TRUE);
        if (category != Category.FAVORITES) {
            favoriteStates.add(Boolean.FALSE);
        }
        return favoriteStates;
    }

    @Transaction
    public CompletableFuture<InsertionType> insertOrUpdate(Recipe recipe) {
        return CompletableFuture.supplyAsync(() -> {
            if (recipe.getRecipeId() == 0) {
                long recipeId = recipeDAO.insert(recipe.getCoreRecipe());
                recipe.getCategories().forEach(category -> recipeDAO.insert(new RecipeCategoryAssociation(recipeId, category.getCategoryId())));
                return InsertionType.INSERT;
            } else {
                recipeDAO.update(recipe.getCoreRecipe());
                List<RecipeCategoryAssociation> recipeCategoryAssociations = recipeDAO.getCategoriesFor(recipe.getRecipeId());
                recipeCategoryAssociations.forEach(recipeCategoryAssociation -> recipeDAO.delete(recipeCategoryAssociation));
                recipe.getCategories().forEach(category -> recipeDAO.insert(new RecipeCategoryAssociation(recipe.getRecipeId(), category.getCategoryId())));
                return InsertionType.UPDATE;
            }
        });
    }

    public CompletableFuture<Void> delete(Recipe recipe) {
        return CompletableFuture.allOf(
                recipeImageService.deleteImage(recipe.getImageName()),
                CompletableFuture.runAsync(() -> recipeDAO.delete(recipe.getCoreRecipe()))
        );
    }

    public CompletableFuture<List<Recipe>> findAll() {
        return CompletableFuture.supplyAsync(() -> recipeDAO.findAll());
    }

    public static class SearchCriteria {
        private Category category = Category.ALL;
        private String searchTerm = "";
        private String seasonalTerm = "";

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public String getSeasonalTerm() {
            return seasonalTerm;
        }

        public void setSeasonalTerm(String seasonalTerm) {
            this.seasonalTerm = seasonalTerm;
        }
    }

}
