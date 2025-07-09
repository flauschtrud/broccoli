package com.flauschcode.broccoli.recipe.importing;

import android.app.Application;
import android.text.Html;
import android.os.Build;
import android.util.Log;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ImportableRecipeBuilder {

    private static final String ID = "@id";
    private static final String TYPE = "@type";
    private static final String HOW_TO_SECTION = "HowToSection";
    private static final String ITEM_LIST_ELEMENT = "itemListElement";

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String RECIPE_INGREDIENT = "recipeIngredient";
    private static final String RECIPE_INSTRUCTIONS = "recipeInstructions";
    private static final String HOW_TO_TEXT = "text";
    private static final String RECIPE_YIELD = "recipeYield";
    private static final String TOTAL_TIME = "totalTime";
    private static final String COOK_TIME = "cookTime";
    private static final String RECIPE_IMAGE = "image";
    private static final String RECIPE_URL = "url";
    private static final String NUTRITION = "nutrition";
    private static final String SERVING_SIZE = "servingSize";
    private static final String CALORIES = "calories";
    private static final String FAT_CONTENT = "fatContent";
    private static final String CARBOHYDRATE_CONTENT = "carbohydrateContent";
    private static final String PROTEIN_CONTENT = "proteinContent";

    private JSONObject recipeJson;

    private final Recipe recipe = new Recipe();
    private final Application application;
    private final RecipeImageService recipeImageService;

    public ImportableRecipeBuilder(Application application, RecipeImageService recipeImageService) {
        this.application = application;
        this.recipeImageService = recipeImageService;
    }

    ImportableRecipeBuilder withRecipeJsonLd(JSONObject jsonObject) {
        recipeJson = jsonObject;
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

        contributeTitle();
        contributeDescription();
        contributeServings();
        contributePreparationTime();
        contributeIngredients();
        contributeDirections();
        contributeNutritionalInformation();
        contributeImage();

        return Optional.of(recipe);
    }

    private String decodeHtmlEntities(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return Arrays.stream(text.split("\n"))
                .map(line -> Html.fromHtml(line, Html.FROM_HTML_MODE_LEGACY).toString())
                .collect(Collectors.joining("\n"));
    }

    private void contributeTitle() {
        recipe.setTitle(decodeHtmlEntities(recipeJson.optString(NAME)));
    }

    private void contributeDescription() {
        recipe.setDescription(decodeHtmlEntities(recipeJson.optString(DESCRIPTION)));
    }

    private void contributeServings() {
        JSONArray yieldArray = recipeJson.optJSONArray(RECIPE_YIELD);
        if (yieldArray == null) {
            recipe.setServings(decodeHtmlEntities(recipeJson.optString(RECIPE_YIELD)));
        } else {
            recipe.setServings(decodeHtmlEntities(yieldArray.optString(0)));
        }
    }

    private void contributePreparationTime() {
        if (recipeJson.has(COOK_TIME)) {
            setParsedAndWordedPreparationTime(COOK_TIME);
        }

        if (recipeJson.has(TOTAL_TIME)) {
            setParsedAndWordedPreparationTime(TOTAL_TIME);
        }
    }

    private void setParsedAndWordedPreparationTime(String jsonLdRecipeCookTime) {
        try {
            Duration duration = Duration.parse(recipeJson.optString(jsonLdRecipeCookTime));
            recipe.setPreparationTime(DurationFormatter.format(duration));
        } catch (DateTimeParseException e) {
            // for rare cases where the JSON contains some time properties with null values
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    private void contributeIngredients() {
        JSONArray ingredientArray = recipeJson.optJSONArray(RECIPE_INGREDIENT);
        if (ingredientArray != null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < ingredientArray.length(); i++) {
                list.add(decodeHtmlEntities(ingredientArray.optString(i)));
            }
            recipe.setIngredients(String.join("\n", list));
        }
    }

    private void contributeDirections() {
        JSONArray instructionsArray = recipeJson.optJSONArray(RECIPE_INSTRUCTIONS);
        if (instructionsArray == null) {
            recipe.setDirections(decodeHtmlEntities(recipeJson.optString(RECIPE_INSTRUCTIONS)));
        } else {
            List<String> directions = new ArrayList<>();
            for (int i = 0; i < instructionsArray.length(); i++) {
                JSONObject instructionObject = instructionsArray.optJSONObject(i);

                if (instructionObject != null && instructionObject.has(TYPE)
                        && HOW_TO_SECTION.equals(instructionObject.optString(TYPE))) {
                    directions.add(contributeSection(instructionObject));
                } else {
                    directions.add(contributeStep(instructionsArray, i));
                }
            }
            recipe.setDirections(String.join("\n", directions));
        }
    }

    private void contributeNutritionalInformation() {
        String nutritionalInformation = contributeNutritionalInformationFor(SERVING_SIZE, R.string.serving)
                + contributeNutritionalInformationFor(CALORIES, R.string.calories)
                + contributeNutritionalInformationFor(FAT_CONTENT, R.string.fat)
                + contributeNutritionalInformationFor(CARBOHYDRATE_CONTENT, R.string.carbohydrates)
                + contributeNutritionalInformationFor(PROTEIN_CONTENT, R.string.protein);

        recipe.setNutritionalValues(nutritionalInformation.strip());
    }

    private String contributeNutritionalInformationFor(String jsonKey, int resourceKey) {
        JSONObject nutritionalValuesObject = recipeJson.optJSONObject(NUTRITION);
        if (nutritionalValuesObject == null) {
            return "";
        }

        String nutritionalValue = decodeHtmlEntities(nutritionalValuesObject.optString(jsonKey));
        if (nutritionalValue.isEmpty()) {
            return "";
        }

        return application.getString(resourceKey) + ": " + nutritionalValue + "\n";
    }

    private String contributeSection(JSONObject instructionObject) {
        List<String> steps = new ArrayList<>();
        steps.add(decodeHtmlEntities(instructionObject.optString(NAME)).toUpperCase());

        JSONArray sectionsArray = instructionObject.optJSONArray(ITEM_LIST_ELEMENT);
        if (sectionsArray != null) {
            for (int i = 0; i < sectionsArray.length(); i++) {
                steps.add(contributeStep(sectionsArray, i));
            }
        }

        return String.join(" ", steps);
    }

    private String contributeStep(JSONArray jsonArray, int position) {
        JSONObject instruction = jsonArray.optJSONObject(position);
        if (instruction == null) {
            return decodeHtmlEntities(jsonArray.optString(position));
        } else {
            return decodeHtmlEntities(instruction.optString(HOW_TO_TEXT));
        }
    }

    private void contributeImage() {
        if(!recipeJson.has(RECIPE_IMAGE)) {
            return;
        }

        URL url;
        try {
            String imageURL = getImageUrl();
            url = new URL(imageURL);
        } catch (MalformedURLException e) {
            Log.e(getClass().getName(), e.getMessage());
            return;
        }

        try {
            String imageName = recipeImageService.downloadToCache(url);
            recipe.setImageName(imageName);
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    private String getImageUrl() {
        JSONArray imagesArray = recipeJson.optJSONArray(RECIPE_IMAGE);
        if (imagesArray != null) {
            return imagesArray.optString(0);
        }

        JSONObject imagesObject = recipeJson.optJSONObject(RECIPE_IMAGE);
        if (imagesObject != null) {
            String url = imagesObject.optString(RECIPE_URL);
            if (!url.isEmpty()) {
                return url;
            }

            return imagesObject.optString(ID); // used by Rank Math schema
        }

        return recipeJson.optString(RECIPE_IMAGE);
    }
}
