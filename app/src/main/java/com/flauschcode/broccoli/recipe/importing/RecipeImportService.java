package com.flauschcode.broccoli.recipe.importing;

import android.app.Application;
import android.util.Log;

import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.inject.Inject;

public class RecipeImportService {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String FIELD_GRAPH = "@graph";
    private static final String FIELD_TYPE = "@type";
    private static final String TYPE_RECIPE = "Recipe";

    private final Application application;
    private final RecipeImageService  recipeImageService;

    @Inject
    public RecipeImportService(Application application, RecipeImageService recipeImageService) {
        this.application = application;
        this.recipeImageService = recipeImageService;
    }

    public CompletableFuture<Optional<Recipe>> importFrom(String url) {
        return CompletableFuture.supplyAsync(() -> {
            Document document;
            try {
                document = Jsoup.connect(url).userAgent(USER_AGENT).get();
            } catch (IOException e) {
                throw new CompletionException(e);
            }

            Elements jsonLds = document.select("script[type=\"application/ld+json\"]");

            Optional<JSONObject> recipeJsonLd = findRecipeIn(jsonLds);
            if (recipeJsonLd.isPresent()) {
                return new ImportableRecipeBuilder(application, recipeImageService).withRecipeJsonLd(recipeJsonLd.get()).from(url).build();
            }

            return Optional.empty();
        });
    }

    private Optional<JSONObject> findRecipeIn(Elements jsonLds) {
        for (Element element : jsonLds) {
            try {

                Object json = new JSONTokener(element.data()).nextValue();

                if (theRecipeIsTheTopLevelObject(json)) {
                    return Optional.of((JSONObject) json);
                }

                if (thereIsAGraphObject(json)) {
                    JSONArray graph = ((JSONObject) json).getJSONArray(FIELD_GRAPH);

                    Optional<JSONObject> optionalRecipe = findRecipeIn(graph);
                    if (optionalRecipe.isPresent()) {
                        return optionalRecipe;
                    }
                }

                if (theTopLevelStructureIsAnArray(json)) {
                    Optional<JSONObject> optionalRecipe = findRecipeIn((JSONArray) json);
                    if (optionalRecipe.isPresent()) {
                        return optionalRecipe;
                    }
                }

            } catch (JSONException e) {
                Log.e(getClass().getName(), e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<JSONObject> findRecipeIn(JSONArray jsonArray) throws JSONException {
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject child = jsonArray.getJSONObject(i);
            if (isRecipe(child)) {
                return Optional.of(child);
            }
        }
        return Optional.empty();
    }

    private boolean isRecipe(JSONObject jsonObject) {
        return jsonObject.has(FIELD_TYPE) && jsonObject.optString(FIELD_TYPE).contains(TYPE_RECIPE);
    }

    private boolean theTopLevelStructureIsAnArray(Object json) {
        return json instanceof JSONArray;
    }

    private boolean thereIsAGraphObject(Object json) {
        return json instanceof JSONObject jsonObject && jsonObject.has(FIELD_GRAPH);
    }

    private boolean theRecipeIsTheTopLevelObject(Object json) {
        return json instanceof JSONObject jsonObject && isRecipe((jsonObject));
    }

}
