package org.flauschhaus.broccoli.recipe.importing;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.inject.Inject;

public class RecipeImportService {

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

            ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(recipeImageService);
            jsonLds.forEach(element -> {
                try {
                    recipeBuilder.withJsonLd(new JSONObject(element.data()));
                } catch (JSONException e) {
                    throw new CompletionException(e);
                }
            });
            recipeBuilder.from(url);

            return recipeBuilder.build();
        });
    }

}
