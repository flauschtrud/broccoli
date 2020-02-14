package org.flauschhaus.broccoli;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Recipe.class}, version = 1)
public abstract class BroccoliDatabase extends RoomDatabase {

    private static BroccoliDatabase broccoliDatabase;

    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract RecipeDAO getRecipeDAO();

    public static synchronized BroccoliDatabase get(Context context) {
        if (broccoliDatabase == null) {
            broccoliDatabase = Room.databaseBuilder(context.getApplicationContext(), BroccoliDatabase.class, "broccoli")
                                    .build();
        }
        return broccoliDatabase;
    }

    public static ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }

}
