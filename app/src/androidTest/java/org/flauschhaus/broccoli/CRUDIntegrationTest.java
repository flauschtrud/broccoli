package org.flauschhaus.broccoli;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.flauschhaus.broccoli.util.RecyclerViewAssertions.hasItemsCount;
import static org.flauschhaus.broccoli.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CRUDIntegrationTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        scenario = launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void crud() {
        onView(withId(R.id.recycler_view)).check(hasItemsCount(0));

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText("Lauchkuchen"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Lauchkuchen")));

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.action_details_edit)).perform(click());
        onView(withId(R.id.new_title)).perform(replaceText("Leckerster Lauchkuchen"));
        onView(withId(R.id.button_save_recipe)).perform(click());
        onView(allOf(withClassName(endsWith("ImageButton")), withParent(withId(R.id.toolbar)))).perform(click());

        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Leckerster Lauchkuchen")));

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.action_details_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.recycler_view)).check(hasItemsCount(0));
    }
}
