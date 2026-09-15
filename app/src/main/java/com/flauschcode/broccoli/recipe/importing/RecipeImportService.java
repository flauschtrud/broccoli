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

            Optional<RecipeMatch> recipeMatch = findRecipeIn(jsonLds);
            if (recipeMatch.isPresent()) {
                return new ImportableRecipeBuilder(application, recipeImageService)
                        .withRecipeJsonLd(recipeMatch.get().recipe())
                        .withGraph(recipeMatch.get().graph())
                        .from(url)
                        .build();
            }

            return Optional.empty();
        });
    }

    private Optional<RecipeMatch> findRecipeIn(Elements jsonLds) {
        for (Element element : jsonLds) {
            try {

                Object json = new JSONTokener(element.data()).nextValue();

                if (theRecipeIsTheTopLevelObject(json)) {
                    return Optional.of(new RecipeMatch((JSONObject) json, null));
                }

                if (thereIsAGraphObject(json)) {
                    JSONArray graph = ((JSONObject) json).getJSONArray(FIELD_GRAPH);

                    Optional<RecipeMatch> recipeMatch = findRecipeIn(graph);
                    if (recipeMatch.isPresent()) {
                        return recipeMatch;
                    }
                }

                if (theTopLevelStructureIsAnArray(json)) {
                    Optional<RecipeMatch> recipeMatch = findRecipeIn((JSONArray) json);
                    if (recipeMatch.isPresent()) {
                        return recipeMatch;
                    }
                }

            } catch (JSONException e) {
                Log.e(getClass().getName(), e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<RecipeMatch> findRecipeIn(JSONArray jsonArray) throws JSONException {
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject child = jsonArray.getJSONObject(i);
            if (isRecipe(child)) {
                return Optional.of(new RecipeMatch(child, jsonArray));
            }
        }
        return Optional.empty();
    }

    /**
     * The graph is the array (e.g. the JSON-LD "@graph") the recipe was found in, if any.
     * It's kept around so sibling nodes (such as an ImageObject referenced from the recipe
     * only via "@id") can still be resolved.
     */
    private record RecipeMatch(JSONObject recipe, JSONArray graph) {}

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
