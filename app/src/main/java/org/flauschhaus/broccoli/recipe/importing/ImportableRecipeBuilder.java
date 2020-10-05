package org.flauschhaus.broccoli.recipe.importing;

import android.util.Log;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ImportableRecipeBuilder {

    private JSONObject recipeJson;
    private Recipe recipe = new Recipe();

    private RecipeImageService recipeImageService;

    public ImportableRecipeBuilder(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    ImportableRecipeBuilder withJsonLd(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("@type") && "Recipe".equals(jsonObject.getString("@type"))) {
            recipeJson = jsonObject;
        }

        return this;
    }

    public ImportableRecipeBuilder from(String url) {
        recipe.setSource(url);
        return this;
    }

    Optional<Recipe> build() {
        if (recipeJson == null) {
            return Optional.empty();
        }

        recipe.setTitle(recipeJson.optString("name"));
        recipe.setServings(recipeJson.optString("recipeYield"));
        recipe.setDescription(recipeJson.optString("description"));
        recipe.setDirections(recipeJson.optString("recipeInstructions"));

        if (recipeJson.has("totalTime")) {
            recipe.setPreparationTime(Period.parse(recipeJson.optString("totalTime")).toString(PeriodFormat.wordBased()));
        }

        JSONArray jsonArray = recipeJson.optJSONArray("recipeIngredient");
        if (jsonArray !=  null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.optString(i));
            }
            recipe.setIngredients(list.stream().collect(Collectors.joining("\n")));
        }

        if(recipeJson.has("image")) {
            String imageURL = recipeJson.optString("image");
            try {
                File tempFile = recipeImageService.createTemporaryFile();
                recipe.setImageName(tempFile.getName());
                recipeImageService.downloadToCache(imageURL, tempFile)
                        .exceptionally(e -> {
                            Log.e(getClass().getName(), e.getMessage());
                            return null;
                        });
            } catch (IOException e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }

        return Optional.of(recipe);
    }

}
