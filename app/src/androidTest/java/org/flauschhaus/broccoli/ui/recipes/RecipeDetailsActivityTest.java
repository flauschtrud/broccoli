package org.flauschhaus.broccoli.ui.recipes;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {

    private View decorView;
    private ActivityScenario<RecipeDetailsActivity> scenario;

    private static Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();

    @Before
    public void setUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RecipeDetailsActivity.class);
        intent.putExtra(Recipe.class.getName(), lauchkuchen);
        scenario = launch(intent);
        scenario.onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void should_display_all_the_data_of_a_recipe() {
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).check(matches(withText(lauchkuchen.getTitle())));
        onView(withId(R.id.details_description)).check(matches(withText(lauchkuchen.getDescription())));

        onView(allOf(withId(R.id.ingredient_text), hasSibling(withText("500"))))
                .check(matches(withText("g Mehl")));
        onView(allOf(withId(R.id.ingredient_text), hasSibling(withText("2"))))
                .check(matches(withText(" Stangen Lauch")));

        onView(allOf(withId(R.id.instruction_text), hasSibling(withText("1"))))
                .check(matches(withText("Lauch schnippeln und Teig machen.")));
        onView(allOf(withId(R.id.instruction_text), hasSibling(withText("2"))))
                .check(matches(withText("Kochen und backen.")));

    }

    @Test
    public void should_show_a_dialog_and_delete_a_recipe() {
        onView(withId(R.id.action_details_delete)).perform(click());
        onView(withText(R.string.dialog_delete_recipe))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        // TODO mock repository
        /*onView(withText(R.string.toast_recipe_deleted))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));*/

    }

}