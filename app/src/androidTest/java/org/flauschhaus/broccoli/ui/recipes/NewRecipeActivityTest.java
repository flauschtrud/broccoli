package org.flauschhaus.broccoli.ui.recipes;

import android.app.Instrumentation;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class NewRecipeActivityTest {

    private View decorView;
    private ActivityScenario<NewRecipeActivity> scenario;

    @Before
    public void setUp() {
        Intents.init();
        scenario = launch(NewRecipeActivity.class);
        scenario.onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @After
    public void tearDown() {
        scenario.close();
        Intents.release();
    }

    @Test
    public void clicking_save_should_return_the_new_recipe() {
        Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));

        onView(withId(R.id.new_image)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText(lauchkuchen.getTitle()));
        onView(withId(R.id.new_description)).perform(typeText(lauchkuchen.getDescription()));
        onView(withId(R.id.new_ingredients)).perform(
                closeSoftKeyboard(),
                typeText("500g Mehl"),
                pressKey(KeyEvent.KEYCODE_ENTER),
                typeText("2 Stangen Lauch")
        );
        onView(withId(R.id.new_directions)).perform(
                closeSoftKeyboard(),
                typeText("1. Lauch schnippeln und Teig machen."),
                pressKey(KeyEvent.KEYCODE_ENTER),
                typeText("2. Kochen und backen.")
        );
        onView(withId(R.id.button_save_recipe)).perform(click()); // TODO find out why there sometimes is such a long wait

        // TODO mock repository
       /* Recipe recipe = (Recipe) scenario.getResult().getResultData().getSerializableExtra(NewRecipeActivity.EXTRA_REPLY);
        assertThat(recipe.getTitle(), is(lauchkuchen.getTitle()));
        assertThat(recipe.getDescription(), is(lauchkuchen.getDescription()));
        assertThat(recipe.getIngredients(), is(lauchkuchen.getIngredients()));
        assertThat(recipe.getDirections(), is(lauchkuchen.getDirections()));
        assertThat(recipe.getImageName(), startsWith("JPEG_"));*/

       // TODO toast
    }

    @Test
    public void clicking_save_without_title_just_shows_toast() {
        onView(withId(R.id.new_title)).perform(typeText("       "));
        onView(withId(R.id.button_save_recipe)).perform(click());

        onView(withText(R.string.toast_title_is_empty))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

}