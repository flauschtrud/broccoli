package com.flauschcode.broccoli.recipe;

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
    @Query("SELECT * FROM recipes WHERE favorite IN (:favorite) ORDER BY title COLLATE NOCASE")
    LiveData<List<Recipe>> findAll(List<Boolean> favorite);

    @Transaction
    @Query(" SELECT recipes.recipeId, title, imageName, description, servings, preparationTime, source, ingredients, directions, favorite FROM recipes INNER JOIN recipes_with_categories ON recipes.recipeId = recipes_with_categories.recipeId WHERE recipes_with_categories.categoryId = :categoryId ORDER BY title COLLATE NOCASE")
    LiveData<List<Recipe>> filterBy(long categoryId);

    @Transaction
    @Query("SELECT * FROM recipes JOIN recipes_fts ON (recipes.recipeId = recipes_fts.docid) WHERE recipes_fts MATCH :term AND favorite IN (:favorite) ORDER BY SUBSTR(OFFSETS(recipes_fts), 1, 1), recipes.title COLLATE NOCASE")
    LiveData<List<Recipe>> searchFor(String term, List<Boolean> favorite);

    @Transaction
    @Query("SELECT recipes.recipeId, recipes.title, imageName, recipes.description, servings, preparationTime, recipes.source, recipes.ingredients, directions, favorite FROM recipes JOIN recipes_fts ON (recipes.recipeId = recipes_fts.docid) INNER JOIN recipes_with_categories ON recipes.recipeId = recipes_with_categories.recipeId WHERE recipes_with_categories.categoryId = :categoryId AND recipes_fts MATCH :term ORDER BY SUBSTR(OFFSETS(recipes_fts), 1, 1), recipes.title COLLATE NOCASE")
    LiveData<List<Recipe>> filterByAndSearchFor(long categoryId, String term);

    @Transaction
    @Query("SELECT * FROM recipes WHERE NOT EXISTS (SELECT * FROM recipes_with_categories WHERE recipeId = recipes.recipeId) ORDER BY title COLLATE NOCASE")
    LiveData<List<Recipe>> findUnassigned();

    @Transaction
    @Query("SELECT * FROM recipes JOIN recipes_fts ON (recipes.recipeId = recipes_fts.docid) WHERE recipes_fts MATCH :term AND NOT EXISTS (SELECT * FROM recipes_with_categories WHERE recipeId = recipes.recipeId) ORDER BY SUBSTR(OFFSETS(recipes_fts), 1, 1), recipes.title COLLATE NOCASE")
    LiveData<List<Recipe>> searchForUnassigned(String term);

    @Transaction
    @Query("SELECT * FROM recipes JOIN recipes_fts ON (recipes.recipeId = recipes_fts.docid) WHERE recipes_fts.ingredients MATCH :seasonalTerm ORDER BY SUBSTR(OFFSETS(recipes_fts), 1, 1), recipes.title COLLATE NOCASE")
    LiveData<List<Recipe>> findSeasonal(String seasonalTerm);

    @Transaction
    @Query("SELECT * FROM recipes JOIN recipes_fts ON (recipes.recipeId = recipes_fts.docid) WHERE recipes_fts MATCH :term AND recipes_fts.rowid IN (SELECT rowid FROM recipes_fts WHERE recipes_fts.ingredients MATCH :seasonalTerm) ORDER BY SUBSTR(OFFSETS(recipes_fts), 1, 1), recipes.title COLLATE NOCASE")
    LiveData<List<Recipe>> searchForSeasonal(String seasonalTerm, String term);

    @Query("SELECT * FROM recipes_with_categories WHERE recipeId == :recipeId")
    List<RecipeCategoryAssociation> getCategoriesFor(long recipeId);

    @Transaction
    @Query("SELECT * FROM recipes")
    List<Recipe> findAll();

}
