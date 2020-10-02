package org.flauschhaus.broccoli.recipe.importing;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

class ImportableRecipeBuilder {

    private JSONObject recipeJson;
    private Recipe recipe = new Recipe();

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

        return Optional.of(recipe);
    }

}
