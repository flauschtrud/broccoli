package org.flauschhaus.broccoli.util;

import org.flauschhaus.broccoli.recipes.Recipe;

public class RecipeTestUtil {

    public static Recipe createLauchkuchen() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setDescription("Das essen wir alle sehr gerne!");
        recipe.setSource("www.dasinternet.de");
        recipe.setServings("4 Portionen");
        recipe.setPreparationTime("50 Minuten");
        recipe.setIngredients("500g Mehl\n2 Stangen Lauch");
        recipe.setDirections("1. Lauch schnippeln und Teig machen.\n2. Kochen und backen.");
        return recipe;
    }

    public static Recipe createNusskuchen() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Nusskuchen");
        recipe.setDescription("Den mögen sogar Nicht-Veganer!");
        recipe.setSource("www.dasinternet.de");
        recipe.setServings("10 Stücke");
        recipe.setPreparationTime("1 Stunde");
        recipe.setIngredients("500g Mehl\nviel Schokolade");
        recipe.setDirections("1. Teig machen.\n2. Backen.\n 3. Schokolade dazu.");
        return recipe;
    }

}
