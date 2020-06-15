package org.flauschhaus.broccoli.recipe;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Insert
    long insert(CoreRecipe recipe);

    @Update
    void update(CoreRecipe recipe);

    @Delete
    void delete(CoreRecipe recipe);

    @Insert
    void insert(RecipeCategoryAssociation recipeCategoryAssociation);

    @Delete
    void delete(RecipeCategoryAssociation recipeCategoryAssociation);

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY title")
    LiveData<List<Recipe>> findAll();

    @Transaction
    @Query(" SELECT recipes.recipeId, title, imageName, description, servings, preparationTime, source, ingredients, directions FROM recipes INNER JOIN recipes_with_categories ON recipes.recipeId = recipes_with_categories.recipeId WHERE recipes_with_categories.categoryId = :categoryId ORDER BY title")
    LiveData<List<Recipe>> filterBy(long categoryId);

    @Transaction
    @Query("SELECT * FROM recipes JOIN recipes_fts ON (recipes.recipeId = recipes_fts.docid) WHERE recipes_fts MATCH :term")
    LiveData<List<Recipe>> searchFor(String term); //TODO order

    @Transaction
    @Query("SELECT * FROM recipes JOIN recipes_fts ON (recipes.recipeId = recipes_fts.docid) INNER JOIN recipes_with_categories ON recipes.recipeId = recipes_with_categories.recipeId WHERE recipes_with_categories.categoryId = :categoryId AND recipes_fts MATCH :term")
    LiveData<List<Recipe>> filterByAndSearchFor(long categoryId, String term); //TODO order

    @Query("SELECT * FROM recipes_with_categories WHERE recipeId == :recipeId")
    List<RecipeCategoryAssociation> getCategoriesFor(long recipeId);

}
