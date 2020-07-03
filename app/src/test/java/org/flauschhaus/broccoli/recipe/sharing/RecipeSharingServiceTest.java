package org.flauschhaus.broccoli.recipe.sharing;

import android.app.Application;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecipeSharingServiceTest {

    @Mock
    private Application application;

    @InjectMocks
    private RecipeSharingService recipeSharingService;

    private String plainTextRecipeFull = "LAUCHKUCHEN\n" +
            "\n" +
            "Servings: 4 Portionen\n" +
            "Preparation time: 1h\n" +
            "\n" +
            "Das ist toll!\n" +
            "\n" +
            "Ingredients:\n" +
            "- 500g Mehl\n" +
            "- 100g Margarine\n" +
            "\n" +
            "Directions:\n" +
            "1. Erst dies.\n" +
            "2. Dann das.\n" +
            "\n" +
            "Shared via Broccoli (TODO insert link)";

    private String plainTextRecipeMinimal = "LAUCHKUCHEN\n" +
            "\n" +
            "Ingredients:\n" +
            "- 500g Mehl\n" +
            "- 100g Margarine\n" +
            "\n" +
            "Directions:\n" +
            "1. Erst dies.\n" +
            "2. Dann das.\n" +
            "\n" +
            "Shared via Broccoli (TODO insert link)";

    @Before
    public void setUp() {
        when(application.getString(R.string.hint_new_recipe_servings)).thenReturn("Servings");
        when(application.getString(R.string.hint_new_recipe_preparation_time)).thenReturn("Preparation time");
        when(application.getString(R.string.ingredients)).thenReturn("Ingredients");
        when(application.getString(R.string.directions)).thenReturn("Directions");
    }

    @Test
    public void to_plain_text_full() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setServings("4 Portionen");
        recipe.setPreparationTime("1h");
        recipe.setDescription("Das ist toll!");
        recipe.setIngredients("- 500g Mehl\n - 100g Margarine  ");
        recipe.setDirections(" 1. Erst dies. \n 2. Dann das. ");

        String result = recipeSharingService.toPlainText(recipe);

        assertThat(result, is(plainTextRecipeFull));
    }

    @Test
    public void to_plain_text_full_minimal() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setIngredients("- 500g Mehl\n - 100g Margarine  ");
        recipe.setDirections(" 1. Erst dies. \n 2. Dann das. ");

        String result = recipeSharingService.toPlainText(recipe);

        assertThat(result, is(plainTextRecipeMinimal));
    }
}