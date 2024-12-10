package com.flauschcode.broccoli;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flauschcode.broccoli.util.RecyclerViewAssertions;
import com.flauschcode.broccoli.util.RecyclerViewMatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CRUDIntegrationTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        AccessibilityChecks.enable();
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
        onView(withId(R.id.new_title)).perform(typeText("Lauchkuche"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        // it's important that instant editing does not create a duplicate of the recipe, see https://github.com/flauschtrud/broccoli/issues/170
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.edit_action)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText("n"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        pressBack();

        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(1));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Lauchkuchen")));

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.edit_action)).perform(click());
        onView(withId(R.id.new_title)).perform(replaceText("Leckerster Lauchkuchen"));
        onView(withId(R.id.button_save_recipe)).perform(click());
        pressBack();

        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(1));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Leckerster Lauchkuchen")));

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.delete_action)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(0));
    }
}
