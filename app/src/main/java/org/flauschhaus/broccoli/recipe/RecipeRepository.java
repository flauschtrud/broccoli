package org.flauschhaus.broccoli.recipe;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeRepository {

    private RecipeDAO recipeDAO;
    private RecipeImageService recipeImageService;

    public enum InsertionType {
        INSERT, UPDATE
    }

    @Inject
    RecipeRepository(RecipeDAO recipeDAO, RecipeImageService recipeImageService) {
        this.recipeDAO = recipeDAO;
        this.recipeImageService = recipeImageService;
    }

    public LiveData<List<Recipe>> find(SearchCriteria criteria) {
        Category category = criteria.getCategory();
        String searchTerm = criteria.getSearchTerm();

        if (category == Category.ALL || category == Category.FAVORITES) {
            List<Boolean> favoriteStates = getChosenFavoriteStates(category);
            return "".equals(searchTerm)? findAll(favoriteStates) : searchFor(searchTerm, favoriteStates);
        }

        if (category == Category.UNASSIGNED) {
            return "".equals(searchTerm)? findUnassigned() : searchForUnassigned(searchTerm);
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
    }

}
