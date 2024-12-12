package com.flauschcode.broccoli;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryDAO;
import com.flauschcode.broccoli.recipe.CoreRecipe;
import com.flauschcode.broccoli.recipe.CoreRecipeFts;
import com.flauschcode.broccoli.recipe.RecipeCategoryAssociation;
import com.flauschcode.broccoli.recipe.RecipeDAO;

@Database(
        version = 2,
        entities = {
                CoreRecipe.class,
                Category.class,
                RecipeCategoryAssociation.class,
                CoreRecipeFts.class
        },
        autoMigrations = {
                @AutoMigration(from = 1, to = 2)
        }
)
public abstract class BroccoliDatabase extends RoomDatabase {

    public abstract RecipeDAO recipeDAO();
    public abstract CategoryDAO categoryDAO();

}
