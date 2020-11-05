package com.flauschcode.broccoli.di;

import android.app.Application;

import androidx.room.Room;

import com.flauschcode.broccoli.BroccoliDatabase;
import com.flauschcode.broccoli.category.CategoryDAO;
import com.flauschcode.broccoli.recipe.RecipeDAO;

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
