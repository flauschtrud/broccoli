package org.flauschhaus.broccoli.di;

import android.app.Application;

import androidx.room.Room;

import org.flauschhaus.broccoli.BroccoliDatabase;
import org.flauschhaus.broccoli.category.CategoryDAO;
import org.flauschhaus.broccoli.recipe.RecipeDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    private BroccoliDatabase database;

    private static final String DB_NAME = "broccoli";

    public DatabaseModule(Application application) {
        database = Room.databaseBuilder(application, BroccoliDatabase.class, DB_NAME)
                .build();
    }

    @Provides
    @Singleton
    BroccoliDatabase database () {
        return database;
    }

    @Provides
    @Singleton
    RecipeDAO recipeDAO(BroccoliDatabase database) {
        return database.getRecipeDAO();
    }

    @Provides
    @Singleton
    CategoryDAO categoryDAO(BroccoliDatabase database) {
        return database.getCategoryDAO();
    }

}
