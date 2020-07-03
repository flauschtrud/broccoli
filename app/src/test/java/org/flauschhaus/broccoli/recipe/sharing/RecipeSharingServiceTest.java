package org.flauschhaus.broccoli.recipe.sharing;

import android.app.Application;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RecipeSharingServiceTest {

    @Mock
    private Application application;

    @InjectMocks
    private RecipeSharingService recipeSharingService;

    private String html = "<h1>Lauchkuchen</h1>" +
            "<p>Servings: 4 Portionen<br/>Preparation time: 1h</p>" +
            "<p>Das ist toll!</p>" +
            "<p>Ingredients:</p>" +
            "<ul>" +
            "<li>500g Mehl</li>" +
            "<li>100g Margarine</li>" +
            "</ul>" +
            "<p>Directions:</p>" +
            "<ol>" +
            "<li>Erst dies.</li>" +
            "<li>Dann das.</li>" +
            "</ol>" +
            "<p>Shared via Broccoli (TODO insert link)</p>";

    @Test
    public void to_html() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setServings("4 Portionen");
        recipe.setPreparationTime("1h"); // TODO test wenn eins davon leer
        recipe.setDescription("Das ist toll!");
        recipe.setIngredients("- 500g Mehl\n - 100g Margarine  ");
        recipe.setDirections(" 1. Erst dies. \n 2. Dann das. ");

        String result = recipeSharingService.toHtml(recipe);

        assertThat(result, is(html));
    }
}