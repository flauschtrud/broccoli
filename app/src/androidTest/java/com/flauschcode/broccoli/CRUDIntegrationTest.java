package com.flauschcode.broccoli;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.flauschcode.broccoli.util.RecyclerViewAssertions;
import com.flauschcode.broccoli.util.RecyclerViewMatcher;

import com.flauschcode.broccoli.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
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
        onView(ViewMatchers.withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(0));

        onView(withId(R.id.fab_recipes)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText("Lauchkuchen"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(1));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Lauchkuchen")));

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_details_edit)).perform(click());        onView(withId(R.id.new_title)).perform(replaceText("Leckerster Lauchkuchen"));
        onView(withId(R.id.button_save_recipe)).perform(click());
        onView(allOf(withClassName(endsWith("ImageButton")), withParent(withId(R.id.toolbar)))).perform(click());

        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(1));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Leckerster Lauchkuchen")));

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(0));
    }
}
