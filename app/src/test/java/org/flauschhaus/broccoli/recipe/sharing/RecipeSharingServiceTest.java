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

    private String html = "<h1>Lauchkuchen</h1>\n" +
            "<p>Servings: 4 Portionen<br /> Preparation time: 1h</p>\n" +
            "<p>Das ist toll!</p>\n" +
            "<p>Ingredients:</p>\n" +
            "<ul>\n" +
            "<li>500g Mehl</li>\n" +
            "<li>100g Margarine</li>\n" +
            "</ul>\n" +
            "<p>Directions:</p>\n" +
            "<ol>\n" +
            "<li>Erst dies.</li>\n" +
            "<li>Dann das.</li>\n" +
            "</ol>\n" +
            "<p>Shared via Broccoli (TODO insert link)</p>";

    @Test
    public void to_html() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setServings("4 Portionen");
        recipe.setPreparationTime("1h"); // TODO test wenn eins davon leer
        recipe.setDescription("Das ist toll!");
        recipe.setIngredients("- 500g Mehl\n - 100g Margarine  ");
        recipe.setDirections(" - Erst dies. \n Dann das. ");

        String result = recipeSharingService.toHtml(recipe);

        assertThat(result, is(html));
    }
}