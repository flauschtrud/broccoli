package com.flauschcode.broccoli.recipe.sharing;

import android.app.Application;
import android.net.Uri;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShareableRecipeBuilderTest {

    @Mock
    private Application application;

    @Mock
    private RecipeImageService recipeImageService;

    @Mock
    private Uri imageUri;

    @InjectMocks
    private ShareableRecipeBuilder shareableRecipeBuilder;

    private static final String PLAIN_TEXT_RECIPE_FULL = "LAUCHKUCHEN\n" +
            "\n" +
            "Servings: 4 Portionen\n" +
            "Preparation time: 1h\n" +
            "Source: www.flauschhaus.org\n" +
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
            "Shared with BROCCOLI_URL";

    private static final String PLAIN_TEXT_RECIPE_MINIMAL = "LAUCHKUCHEN\n" +
            "\n" +
            "Ingredients:\n" +
            "- 500g Mehl\n" +
            "- 100g Margarine\n" +
            "\n" +
            "Directions:\n" +
            "1. Erst dies.\n" +
            "2. Dann das.\n" +
            "\n" +
            "Shared with BROCCOLI_URL";

    @Before
    public void setUp() {
        when(application.getString(R.string.servings)).thenReturn("Servings");
        when(application.getString(R.string.preparation_time)).thenReturn("Preparation time");
        when(application.getString(R.string.source)).thenReturn("Source");
        when(application.getString(R.string.ingredients)).thenReturn("Ingredients");
        when(application.getString(R.string.directions)).thenReturn("Directions");
        when(application.getString(R.string.store_url)).thenReturn("BROCCOLI_URL");
        when(application.getString(R.string.shared_with,  "BROCCOLI_URL")).thenReturn("Shared with BROCCOLI_URL");
    }

    @Test
    public void to_plain_text_full() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setServings("4 Portionen");
        recipe.setPreparationTime("1h");
        recipe.setDescription("Das ist toll!");
        recipe.setSource("www.flauschhaus.org");
        recipe.setIngredients("- 500g Mehl\n - 100g Margarine  ");
        recipe.setDirections(" 1. Erst dies. \n 2. Dann das. ");
        recipe.setImageName("image/bla.jpg");

        when(recipeImageService.getUri("image/bla.jpg")).thenReturn(imageUri);

        ShareableRecipe result = shareableRecipeBuilder.from(recipe);

        assertThat(result.getPlainText(), is(PLAIN_TEXT_RECIPE_FULL));
        assertThat(result.getImageUri(), is(imageUri));
    }

    @Test
    public void to_plain_text_full_minimal() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setIngredients("- 500g Mehl\n - 100g Margarine  ");
        recipe.setDirections(" 1. Erst dies. \n 2. Dann das. ");

        ShareableRecipe result = shareableRecipeBuilder.from(recipe);

        verifyNoInteractions(recipeImageService);
        assertThat(result.getPlainText(), is(PLAIN_TEXT_RECIPE_MINIMAL));
        assertThat(result.getImageUri(), is(Uri.EMPTY));
    }
}