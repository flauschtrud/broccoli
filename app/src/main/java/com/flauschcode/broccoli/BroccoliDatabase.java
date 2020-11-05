package com.flauschcode.broccoli;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryDAO;
import com.flauschcode.broccoli.recipe.CoreRecipe;
import com.flauschcode.broccoli.recipe.CoreRecipeFts;
import com.flauschcode.broccoli.recipe.RecipeCategoryAssociation;
import com.flauschcode.broccoli.recipe.RecipeDAO;

@Database(entities = {CoreRecipe.class, Category.class, RecipeCategoryAssociation.class, CoreRecipeFts.class}, version = 1)
public abstract class BroccoliDatabase extends RoomDatabase {

    private static BroccoliDatabase broccoliDatabase;

    public abstract RecipeDAO getRecipeDAO();
    public abstract CategoryDAO getCategoryDAO();

    public static synchronized BroccoliDatabase get(Context context) {
        if (broccoliDatabase == null) {
            broccoliDatabase = Room.databaseBuilder(context.getApplicationContext(), BroccoliDatabase.class, "broccoli")
                                    .build();
        }
        return broccoliDatabase;
    }

}
