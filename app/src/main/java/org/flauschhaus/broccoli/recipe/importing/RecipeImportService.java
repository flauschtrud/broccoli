package org.flauschhaus.broccoli.recipe.importing;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    private static final String FIELD_GRAPH = "@graph";
    private static final String FIELD_TYPE = "@type";
    private static final String TYPE_RECIPE = "Recipe";

    private RecipeImageService recipeImageService;

    @Inject
    public RecipeImportService(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    public CompletableFuture<Optional<Recipe>> importFrom(String url) {
        return CompletableFuture.supplyAsync(() -> {
            Document document;
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                throw new CompletionException(e);
            }

            Elements jsonLds = document.select("script[type=\"application/ld+json\"]");

            Optional<JSONObject> recipeJsonLd = findRecipe(jsonLds);
            if (recipeJsonLd.isPresent()) {
                return new ImportableRecipeBuilder(recipeImageService).withRecipeJsonLd(recipeJsonLd.get()).from(url).build();
            }

            return Optional.empty();
        });
    }

    private Optional<JSONObject> findRecipe(Elements jsonLds) {
        for (Element element : jsonLds) {
            try {

                JSONObject jsonObject = new JSONObject(element.data());
                if (isRecipe(jsonObject)) {
                    return Optional.of(jsonObject);
                }

                if (jsonObject.has(FIELD_GRAPH)) {
                    JSONArray graph = jsonObject.getJSONArray(FIELD_GRAPH);

                    for (int i=0; i<graph.length(); i++) {
                        JSONObject child = graph.getJSONObject(i);
                        if (isRecipe(child)) {
                            return Optional.of(child);
                        }
                    }

                }
            } catch (JSONException e) {
                throw new CompletionException(e);
            }
        }
        return Optional.empty();
    }

    private boolean isRecipe(JSONObject jsonObject) {
        return jsonObject.has(FIELD_TYPE) && TYPE_RECIPE.equals(jsonObject.optString(FIELD_TYPE));
    }

}
