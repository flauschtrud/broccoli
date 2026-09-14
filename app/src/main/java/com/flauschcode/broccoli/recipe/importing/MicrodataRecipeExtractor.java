package com.flauschcode.broccoli.recipe.importing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

/**
 * Some sites expose their recipe data as schema.org Microdata (itemscope/itemtype/itemprop
 * attributes on the regular page HTML) instead of, or in addition to, JSON-LD. This walks such
 * a Microdata Recipe item and converts it into a JSONObject shaped like the equivalent JSON-LD,
 * so it can be handed to ImportableRecipeBuilder unchanged.
 */
class MicrodataRecipeExtractor {

    private static final String ITEMSCOPE = "itemscope";
    private static final String ITEMTYPE = "itemtype";
    private static final String ITEMPROP = "itemprop";
    private static final String SCHEMA_RECIPE = "schema.org/Recipe";
    private static final String SCHEMA_NUTRITION_INFORMATION = "schema.org/NutritionInformation";

    static Optional<JSONObject> extract(Document document) {
        Element recipeScope = findItemScope(document, SCHEMA_RECIPE);
        if (recipeScope == null) {
            return Optional.empty();
        }

        try {
            JSONObject recipe = new JSONObject();
            recipe.put("@type", "Recipe");

            putIfPresent(recipe, "name", firstValue(recipeScope, "name"));
            putIfPresent(recipe, "description", firstValue(recipeScope, "description"));
            putIfPresent(recipe, "recipeYield", firstValue(recipeScope, "recipeYield"));
            putIfPresent(recipe, "cookTime", firstValue(recipeScope, "cookTime"));
            putIfPresent(recipe, "totalTime", firstValue(recipeScope, "totalTime"));
            putIfPresent(recipe, "image", firstValue(recipeScope, "image"));

            JSONArray ingredients = collectIngredients(recipeScope);
            if (ingredients.length() > 0) {
                recipe.put("recipeIngredient", ingredients);
            }

            JSONArray instructions = collectInstructions(recipeScope);
            if (instructions.length() > 0) {
                recipe.put("recipeInstructions", instructions);
            }

            JSONObject nutrition = collectNutrition(recipeScope);
            if (nutrition.length() > 0) {
                recipe.put("nutrition", nutrition);
            }

            boolean hasContent = ingredients.length() > 0 || instructions.length() > 0;
            return hasContent ? Optional.of(recipe) : Optional.empty();
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    private static Element findItemScope(Document document, String schemaType) {
        for (Element element : document.select("[" + ITEMSCOPE + "][" + ITEMTYPE + "]")) {
            if (element.attr(ITEMTYPE).contains(schemaType)) {
                return element;
            }
        }
        return null;
    }

    private static String firstValue(Element scope, String propertyName) {
        Elements matches = scope.select("[" + ITEMPROP + "=" + propertyName + "]");
        return matches.isEmpty() ? null : propertyValue(matches.first());
    }

    private static JSONArray collectIngredients(Element recipeScope) {
        JSONArray ingredients = new JSONArray();
        for (Element ingredient : recipeScope.select("[" + ITEMPROP + "=recipeIngredient]")) {
            addNonEmpty(ingredients, ingredient.text());
        }
        return ingredients;
    }

    /**
     * "recipeInstructions" is sometimes repeated once per step (one itemprop-bearing element
     * per step), and sometimes present once on a container whose children are the steps (e.g.
     * a <ul> of <li> steps). Both are valid, so we handle both.
     */
    private static JSONArray collectInstructions(Element recipeScope) {
        JSONArray instructions = new JSONArray();
        Elements instructionElements = recipeScope.select("[" + ITEMPROP + "=recipeInstructions]");

        if (instructionElements.size() > 1) {
            for (Element step : instructionElements) {
                addNonEmpty(instructions, step.text());
            }
            return instructions;
        }

        if (instructionElements.isEmpty()) {
            return instructions;
        }

        Element container = instructionElements.first();
        Elements steps = container.children();
        if (steps.isEmpty()) {
            addNonEmpty(instructions, container.text());
        } else {
            for (Element step : steps) {
                addNonEmpty(instructions, step.text());
            }
        }
        return instructions;
    }

    private static JSONObject collectNutrition(Element recipeScope) throws JSONException {
        JSONObject nutrition = new JSONObject();
        Elements nutritionMatches = recipeScope.select("[" + ITEMPROP + "=nutrition]");
        if (nutritionMatches.isEmpty()) {
            return nutrition;
        }

        Element nutritionScope = nutritionMatches.first();
        if (!nutritionScope.attr(ITEMTYPE).contains(SCHEMA_NUTRITION_INFORMATION)) {
            return nutrition;
        }

        putIfPresent(nutrition, "servingSize", firstValue(nutritionScope, "servingSize"));
        putIfPresent(nutrition, "calories", firstValue(nutritionScope, "calories"));
        putIfPresent(nutrition, "fatContent", firstValue(nutritionScope, "fatContent"));
        putIfPresent(nutrition, "carbohydrateContent", firstValue(nutritionScope, "carbohydrateContent"));
        putIfPresent(nutrition, "proteinContent", firstValue(nutritionScope, "proteinContent"));
        return nutrition;
    }

    /**
     * Per the Microdata spec, an itemprop's value depends on the element it's on: meta/time
     * elements carry it in an attribute, media/link elements resolve a URL attribute, and
     * anything else uses its text content.
     */
    private static String propertyValue(Element element) {
        switch (element.tagName()) {
            case "meta":
                return element.attr("content");
            case "time":
                String datetime = element.attr("datetime");
                return datetime.isEmpty() ? element.text() : datetime;
            case "img":
            case "audio":
            case "embed":
            case "iframe":
            case "source":
            case "track":
            case "video":
                return element.absUrl("src");
            case "a":
            case "area":
            case "link":
                return element.absUrl("href");
            default:
                return element.text();
        }
    }

    private static void addNonEmpty(JSONArray array, String text) {
        String trimmed = text == null ? "" : text.trim();
        if (!trimmed.isEmpty()) {
            array.put(trimmed);
        }
    }

    private static void putIfPresent(JSONObject json, String key, String value) throws JSONException {
        if (value != null && !value.trim().isEmpty()) {
            json.put(key, value.trim());
        }
    }

    private MicrodataRecipeExtractor() {}
}
