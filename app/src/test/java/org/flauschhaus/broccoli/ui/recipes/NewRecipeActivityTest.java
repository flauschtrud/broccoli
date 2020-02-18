package org.flauschhaus.broccoli.ui.recipes;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class NewRecipeActivityTest {

    @Test
    public void clicking_save_should_return_the_new_recipe() {
        ActivityScenario<NewRecipeActivity> scenario = launch(NewRecipeActivity.class);

        scenario.onActivity(activity -> {
            onView(withId(R.id.new_title)).perform(typeText("Lauchkuchen"));
            onView(withId(R.id.new_description)).perform(typeText("Das essen wir alle sehr gerne!"));
            onView(withId(R.id.button_save_recipe)).perform(click());

            assertThat(activity.isFinishing(), is(true));
        });

        Recipe recipe = (Recipe) scenario.getResult().getResultData().getSerializableExtra(NewRecipeActivity.EXTRA_REPLY);
        assertThat(recipe.getTitle(), is("Lauchkuchen"));
        assertThat(recipe.getDescription(), is("Das essen wir alle sehr gerne!"));
    }

}