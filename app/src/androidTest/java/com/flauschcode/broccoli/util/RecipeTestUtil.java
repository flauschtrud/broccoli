package com.flauschcode.broccoli.util;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.recipe.Recipe;

public class RecipeTestUtil {

    public static Recipe createLauchkuchen() {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(0);
        recipe.setTitle("Lauchkuchen");
        recipe.setDescription("Das essen wir alle sehr gerne!");
        recipe.setSource("www.dasinternet.de");
        recipe.setServings("4 Portionen");
        recipe.setPreparationTime("50 Minuten");
        recipe.setIngredients("500g Mehl\n2 Stangen Lauch");
        recipe.setDirections("1. Lauch schnippeln und Teig machen.\n2. Kochen und backen.");
        recipe.setNotes("Ein paar Anmerkungen zum Lauchkuchen.");
        recipe.getCategories().add(new Category("Hauptgerichte"));
        recipe.getCategories().add(new Category("Gebackenes"));
        return recipe;
    }

    public static Recipe createdAlreadySavedLauchkuchen() {
        Recipe recipe = createLauchkuchen();
        recipe.setRecipeId(1);
        recipe.setImageName("lauchkuchen.jpg");
        recipe.addCategory(new Category("Hauptgerichte"));
        return recipe;
    }

    public static Recipe createNusskuchen() {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(2);
        recipe.setImageName("nusskuchen.jpg");
        recipe.setTitle("Nusskuchen");
        recipe.setDescription("Den mögen sogar Nicht-Veganer!");
        recipe.setSource("www.dasinternet.de");
        recipe.setServings("10 Stücke");
        recipe.setPreparationTime("1 Stunde");
        recipe.setIngredients("500g Mehl\nviel Schokolade");
        recipe.setDirections("1. Teig machen.\n2. Backen.\n 3. Schokolade dazu.");
        recipe.setNotes("Ein paar Anmerkungen zum Nusskuchen.");
        return recipe;
    }

}
