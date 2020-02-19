package org.flauschhaus.broccoli.ui.recipes;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowToast;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@RunWith(AndroidJUnit4.class)
@LooperMode(PAUSED)
public class RecipesFragmentTest {

    @Test
    public void click_on_fab_should_trigger_new_recipe_activity() {
        FragmentScenario<RecipesFragment> scenario = launchInContainer(RecipesFragment.class);

        scenario.onFragment(fragment -> {
            onView(withId(R.id.fab)).perform(click());

            Intent expected = new Intent(fragment.getActivity(), NewRecipeActivity.class);
            Intent actual = shadowOf(fragment.getActivity()).peekNextStartedActivity();

            assertThat(actual.getComponent(), is(expected.getComponent()));
        });

    }

    @Test
    @Ignore // did not manage to make RecyclerView accessible for Robolectric :(
    public void received_recipe_should_be_added_to_list() {
        Recipe recipe = RecipeTestUtil.createLauchkuchen();

        FragmentScenario<RecipesFragment> scenario = launchInContainer(RecipesFragment.class);

        scenario.onFragment(fragment -> {
            fragment.onActivityResult(
                    RecipesFragment.NEW_RECIPE_ACTIVITY_REQUEST_CODE,
                    Activity.RESULT_OK,
                    new Intent().putExtra(NewRecipeActivity.EXTRA_REPLY, recipe));

            assertThat(ShadowToast.getTextOfLatestToast(), is(fragment.getString(R.string.toast_new_recipe)));

            onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
            //onView(withId(R.id.recycler_view)).check(hasItemsCount(1)); // does not work, returns 0
        });

    }

}