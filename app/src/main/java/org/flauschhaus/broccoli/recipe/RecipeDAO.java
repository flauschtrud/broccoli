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

    @Insert
    void insert(RecipeCategoryAssociation recipeCategoryAssociation);

    @Update
    void update(CoreRecipe recipe);

    @Delete
    void delete(CoreRecipe recipe);

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY title")
    LiveData<List<Recipe>> findAll();


}
