package org.flauschhaus.broccoli;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.category.CategoryDAO;
import org.flauschhaus.broccoli.recipe.CoreRecipe;
import org.flauschhaus.broccoli.recipe.CoreRecipeFts;
import org.flauschhaus.broccoli.recipe.RecipeCategoryAssociation;
import org.flauschhaus.broccoli.recipe.RecipeDAO;

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
