package org.flauschhaus.broccoli.recipe.sharing;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.flauschhaus.broccoli.recipe.Recipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.inject.Inject;

public class ShareRecipeAsFileService {

    private static final String AUTHORITY = "org.flauschhaus.broccoli.fileprovider";

    private Application application;

    @Inject
    public ShareRecipeAsFileService(Application application) {
        this.application = application;
    }

    public Uri shareAsFile(Recipe recipe) throws IOException {
        String title = recipe.getTitle();
        String jsonFileName = title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".json";
        File temporaryFile = new File(application.getCacheDir(), jsonFileName);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(temporaryFile, recipe);

        return FileProvider.getUriForFile(application, AUTHORITY, temporaryFile);
    }

    public Optional<Recipe> loadFromFile(Uri uri) {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream;
        try {
            inputStream = application.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), e.getMessage());
            return Optional.empty();
        }

        Recipe recipe;
        try {
            recipe = objectMapper.readValue(inputStream, Recipe.class);
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
            return Optional.empty();
        }

        recipe.getCategories().clear();
        recipe.setFavorite(false);
        recipe.setImageName("");
        return Optional.of(recipe);
    }

}
