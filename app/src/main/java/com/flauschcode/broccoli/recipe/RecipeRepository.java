package com.flauschcode.broccoli.recipe;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.seasons.SeasonalCalendar;
import com.flauschcode.broccoli.seasons.SeasonalCalendarHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeRepository {

    private final RecipeDAO recipeDAO;
    private final RecipeImageService recipeImageService;
    private final SeasonalCalendarHolder seasonalCalendarHolder;
    private final CategoryRepository categoryRepository;

    @Inject
    RecipeRepository(RecipeDAO recipeDAO, RecipeImageService recipeImageService, SeasonalCalendarHolder seasonalCalendarHolder, CategoryRepository categoryRepository) {
        this.recipeDAO = recipeDAO;
        this.recipeImageService = recipeImageService;
        this.seasonalCalendarHolder = seasonalCalendarHolder;
        this.categoryRepository = categoryRepository;
    }

    public LiveData<List<Recipe>> find(SearchCriteria criteria) {
        Category category = criteria.getCategory();
        String searchTerm = criteria.getSearchTerm();

        if (!criteria.getSeasonalTerms().isEmpty()) {
            String wildcardQuery = getSanitizedWildcardQuery(searchTerm);
            String seasonalTerm = buildQueryFor(criteria.getSeasonalTerms());
            return "".equals(searchTerm)? recipeDAO.findSeasonal(seasonalTerm) : recipeDAO.searchForSeasonal(seasonalTerm, wildcardQuery);
        }

        if (category.equals(categoryRepository.getAllRecipesCategory()) || category.equals(categoryRepository.getFavoritesCategory())) {
            List<Boolean> favoriteStates = getChosenFavoriteStates(category);
            return "".equals(searchTerm)? findAll(favoriteStates) : searchFor(searchTerm, favoriteStates);
        }

        if (category.equals(categoryRepository.getUnassignedRecipesCategory())) {
            return "".equals(searchTerm)? findUnassigned() : searchForUnassigned(searchTerm);
        }

        if (category.equals(categoryRepository.getSeasonalRecipesCategory())) {
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
        String wildcardQuery = getSanitizedWildcardQuery(term);
        return recipeDAO.searchFor(wildcardQuery, favoritesList);
    }

    private LiveData<List<Recipe>> filterByAndSearchFor(Category category, String term) {
        String wildcardQuery = getSanitizedWildcardQuery(term);
        return recipeDAO.filterByAndSearchFor(category.getCategoryId(), wildcardQuery);
    }

    private LiveData<List<Recipe>> findUnassigned() {
        return recipeDAO.findUnassigned();
    }

    private LiveData<List<Recipe>> searchForUnassigned(String term) {
        String wildcardQuery = getSanitizedWildcardQuery(term);
        return recipeDAO.searchForUnassigned(wildcardQuery);
    }

    private LiveData<List<Recipe>> findSeasonal() {
        String seasonalSearchTerm = getSeasonalSearchTerm();
        return recipeDAO.findSeasonal(seasonalSearchTerm);
    }

    private LiveData<List<Recipe>> searchForSeasonal(String term) {
        String wildcardQuery = getSanitizedWildcardQuery(term);
        String seasonalSearchTerm = getSeasonalSearchTerm();
        return recipeDAO.searchForSeasonal(seasonalSearchTerm, wildcardQuery);
    }

    private static String getSanitizedWildcardQuery(String term) {
        String trailingDashesRemoved = term.replaceFirst("^-+", "");
        String quotesEscaped = trailingDashesRemoved.replace("\"", "");
        return String.format("%s*", quotesEscaped);
    }

    private String getSeasonalSearchTerm() {
        Optional<SeasonalCalendar> seasonalCalendarOptional = seasonalCalendarHolder.get();
        if (seasonalCalendarOptional.isPresent()) {
            SeasonalCalendar seasonalCalendar = seasonalCalendarOptional.get();
            return buildQueryFor(seasonalCalendar.getSearchTermsForCurrentMonth());
        }
        return "";
    }

    private String buildQueryFor(Collection<String> seasonalTerms) {
        return seasonalTerms.stream().map(term -> "\"" + term + "\"").collect(Collectors.joining(" OR "));
    }

    private List<Boolean> getChosenFavoriteStates(Category category) {
        List<Boolean> favoriteStates = new ArrayList<>();
        favoriteStates.add(Boolean.TRUE);
        if (!category.equals(categoryRepository.getFavoritesCategory())) {
            favoriteStates.add(Boolean.FALSE);
        }
        return favoriteStates;
    }

    @Transaction
    public CompletableFuture<Long> insertOrUpdate(Recipe recipe) {
        return CompletableFuture.supplyAsync(() -> {
            if (recipe.getRecipeId() == 0) {
                long recipeId = recipeDAO.insert(recipe.getCoreRecipe());
                recipe.getCategories().forEach(category -> recipeDAO.insert(new RecipeCategoryAssociation(recipeId, category.getCategoryId())));
                return recipeId;
            } else {
                recipeDAO.update(recipe.getCoreRecipe());
                List<RecipeCategoryAssociation> recipeCategoryAssociations = recipeDAO.getCategoriesFor(recipe.getRecipeId());
                recipeCategoryAssociations.forEach(recipeDAO::delete);
                recipe.getCategories().forEach(category -> recipeDAO.insert(new RecipeCategoryAssociation(recipe.getRecipeId(), category.getCategoryId())));
                return recipe.getRecipeId();
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
        return CompletableFuture.supplyAsync(recipeDAO::findAll);
    }

    public static class SearchCriteria {
        private Category category;
        private String searchTerm;
        private List<String> seasonalTerms;

        public SearchCriteria(Category category, String searchTerm, List<String> seasonalTerms) {
            this.category = category;
            this.searchTerm = searchTerm;
            this.seasonalTerms = seasonalTerms;
        }

        public Category getCategory() {
            return category;
        }

        public String getSearchTerm() {
            return searchTerm;
        }

        public List<String> getSeasonalTerms() {
            return seasonalTerms;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public void setSeasonalTerms(List<String> seasonalTerms) {
            this.seasonalTerms = seasonalTerms;
        }
    }

}
