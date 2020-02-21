package org.flauschhaus.broccoli.ui.recipes;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@RunWith(AndroidJUnit4.class)
@LooperMode(PAUSED)
public class RecipeDetailsActivityTest {

    @Test
    public void should_display_all_the_data_of_a_recipe() {

        Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RecipeDetailsActivity.class);
        intent.putExtra(Recipe.class.getName(), lauchkuchen);

        ActivityScenario<RecipeDetailsActivity> scenario = launch(intent);

        scenario.onActivity(activity -> {
            onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).check(matches(withText(lauchkuchen.getTitle())));
            onView(withId(R.id.details_description)).check(matches(withText(lauchkuchen.getDescription())));

            onView(allOf(withId(R.id.instruction_text), hasSibling(withText("1"))))
                    .check(matches(withText("Lauch schnippeln und Teig machen.")));
            onView(allOf(withId(R.id.instruction_text), hasSibling(withText("2"))))
                    .check(matches(withText("Kochen und backen.")));
        });
    }

}