package com.flauschcode.broccoli;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.NavigationViewActions.navigateTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.flauschcode.broccoli.util.CustomViewActions.waitFor;
import static org.hamcrest.Matchers.allOf;

import android.view.Gravity;
import android.view.View;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.flauschcode.broccoli.util.RecyclerViewAssertions;
import com.flauschcode.broccoli.util.RecyclerViewMatcher;
import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SeasonsIntegrationTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        // at the moment I can't really deal with a 48dp high toolbar button
        //AccessibilityChecks.enable();

        Set<String> languages = new HashSet<>();
        languages.add("de");

        PreferenceManager.getDefaultSharedPreferences(getApplication())
                .edit()
                .putString("seasonal-calendar-region", "flauschland")
                .putStringSet("seasonal-calendar-languages", languages)
                .apply();

        scenario = launch(MainActivity.class);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void find_seasonal_recipes() {
        onView(ViewMatchers.withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(0));

        // create recipe
        onView(withId(R.id.fab_recipes)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText("Flauschrezept"));
        onView(withId(android.R.id.content)).perform(closeSoftKeyboard(), swipeUp()); // scrollTo() does not work for NestedScrollViews
        onView(withId(R.id.new_ingredients)).perform(typeText("2 flauschfrucht"));
        onView(withId(R.id.button_save_recipe)).perform(click());
        pressBack();

        // check show only this recipe
        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(1));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Flauschrezept")));

        // create another recipe
        onView(withId(R.id.fab_recipes)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText("Zweites Rezept"));
        onView(withId(R.id.button_save_recipe)).perform(click());
        pressBack();

        // check show both recipes
        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(2));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.card_text_view_title)).check(matches(withText("Zweites Rezept")));

        // go to seasonal calendar
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(open());
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_seasons));

        onView(isRoot()).perform(waitFor(1000));

        // go to the month of November
        onView(withId(R.id.seasons_tablayout)).perform(selectTabAtPosition(10));
        onView(isRoot()).perform(waitFor(1000));

        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.seasonal_food_name)).check(matches(withText("Apples")));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.seasonal_food_name)).check(matches(withText("flauschfrucht")));

        onView(isRoot()).perform(waitFor(1000));

        // choose flauschfrucht
        onView(allOf(isDisplayed(), withId(R.id.recycler_view))).perform(actionOnItemAtPosition(1, click()));

        // check show only this recipe
        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(1));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Flauschrezept")));

        // remove filter
        onView(withId(R.id.toolbar_button)).perform(click());
        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(2));

        // delete recipes
        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.delete_action)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.delete_action)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(0));
    }

    private static ViewAction selectTabAtPosition(final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription() {
                return "with tab at index" + position;
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view instanceof TabLayout) {
                    TabLayout tabLayout = (TabLayout) view;
                    TabLayout.Tab tab = tabLayout.getTabAt(position);

                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        };
    }

}
