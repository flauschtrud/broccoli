package org.flauschhaus.broccoli;

import android.app.Activity;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.ui.recipes.NewRecipeActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowToast;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@RunWith(AndroidJUnit4.class)
@LooperMode(PAUSED)
public class MainActivityTest {

    @Test
    public void click_on_fab_should_trigger_new_recipe_activity() {
        ActivityScenario<MainActivity> scenario = launch(MainActivity.class);

        scenario.onActivity(activity -> {
            onView(withId(R.id.fab)).perform(click());

            Intent expected = new Intent(activity, NewRecipeActivity.class);
            Intent actual = shadowOf(activity).peekNextStartedActivity();

            assertThat(actual.getComponent(), is(expected.getComponent()));
        });

    }

    @Test
    public void received_recipe_should_be_added_to_list() {
        Recipe recipe = RecipeTestUtil.createLauchkuchen();

        ActivityScenario<MainActivity> scenario = launch(MainActivity.class);

        scenario.onActivity(activity -> {
            shadowOf(activity).callOnActivityResult(
                    MainActivity.NEW_RECIPE_ACTIVITY_REQUEST_CODE,
                    Activity.RESULT_OK,
                    new Intent().putExtra(NewRecipeActivity.EXTRA_REPLY, recipe));

            assertThat(ShadowToast.getTextOfLatestToast(), is(activity.getString(R.string.toast_new_recipe)));
            onView(withId(R.id.text_recipes))
                    .check(matches(isDisplayed()))
                    .check(matches(withSubstring(recipe.getTitle())))
                    .check(matches(withSubstring(recipe.getDescription())));
        });

    }

}