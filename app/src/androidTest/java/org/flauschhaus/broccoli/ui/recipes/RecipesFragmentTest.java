package org.flauschhaus.broccoli.ui.recipes;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.flauschhaus.broccoli.util.RecyclerViewAssertions.hasItemsCount;
import static org.flauschhaus.broccoli.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RecipesFragmentTest {

    private View decorView;
    private FragmentScenario<RecipesFragment> scenario;

    private final Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();

    @Before
    public void setUp() {
        Intents.init();
        scenario = launchInContainer(RecipesFragment.class);
        scenario.onFragment(fragment -> decorView = fragment.getActivity().getWindow().getDecorView());
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void click_on_fab_should_trigger_new_recipe_activity() {
        onView(withId(R.id.fab)).perform(click());
        intended(hasComponent(NewRecipeActivity.class.getName()));
    }

    @Test
    @Ignore("Testing on Android sucks...")
    public void received_recipe_should_be_added_to_list() throws InterruptedException {
        scenario.onFragment(fragment -> {
            fragment.onActivityResult(
                    RecipesFragment.NEW_RECIPE_ACTIVITY_REQUEST_CODE,
                    Activity.RESULT_OK,
                    new Intent().putExtra(NewRecipeActivity.EXTRA_REPLY, lauchkuchen));
        });

        onView(withText(R.string.toast_new_recipe))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(1000); // TODO don't do this and rather try to understand idling resources

        onView(withId(R.id.recycler_view)).check(hasItemsCount(1)); // TODO seems to be 2
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText(lauchkuchen.getTitle())));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_description)).check(matches(withText(lauchkuchen.getDescription())));

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(1, click()));

        intended(allOf(
                hasComponent(RecipeDetailsActivity.class.getName()),
                hasExtra(Recipe.class.getName(), lauchkuchen)
        ));
    }

}