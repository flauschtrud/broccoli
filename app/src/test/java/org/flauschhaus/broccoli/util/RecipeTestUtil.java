package org.flauschhaus.broccoli.util;

import org.flauschhaus.broccoli.recipes.Recipe;

public class RecipeTestUtil {

    public static Recipe createLauchkuchen() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setDescription("Das essen wir alle sehr gerne!");
        return recipe;
    }

    public static Recipe createHummus() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Hummus");
        recipe.setDescription("Leckerer Kichererbsenmatsch!");
        return recipe;
    }
}
