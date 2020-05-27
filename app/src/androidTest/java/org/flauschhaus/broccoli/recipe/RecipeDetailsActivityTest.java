package org.flauschhaus.broccoli.recipe;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.DaggerMockApplicationComponent;
import org.flauschhaus.broccoli.MockApplicationComponent;
import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.details.RecipeDetailsActivity;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {

    @Inject
    RecipeRepository recipeRepository;

    private ActivityScenario<RecipeDetailsActivity> scenario;

    private static Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();

    @Before
    public void setUp() {
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        Intents.init();

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RecipeDetailsActivity.class);
        intent.putExtra(Recipe.class.getName(), lauchkuchen);
        scenario = launch(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
        scenario.close();
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void display_all_the_data_of_a_recipe() {
        onView(withId(R.id.toolbar_layout)).check(matches(withContentDescription(lauchkuchen.getTitle())));

        onView(withId(R.id.details_description)).check(matches(withText(lauchkuchen.getDescription())));
        onView(withId(R.id.details_source)).check(matches(withText(lauchkuchen.getSource())));
        onView(withId(R.id.details_servings)).check(matches(withText(lauchkuchen.getServings())));
        onView(withId(R.id.details_preparation_time)).check(matches(withText(lauchkuchen.getPreparationTime())));

        onView(allOf(withId(R.id.ingredient_text), hasSibling(withText("500"))))
                .check(matches(withText("g Mehl")));
        onView(allOf(withId(R.id.ingredient_text), hasSibling(withText("2"))))
                .check(matches(withText(" Stangen Lauch")));

        onView(allOf(withId(R.id.direction_text), hasSibling(withText("1"))))
                .check(matches(withText("Lauch schnippeln und Teig machen.")));
        onView(allOf(withId(R.id.direction_text), hasSibling(withText("2"))))
                .check(matches(withText("Kochen und backen.")));
    }

    @Test
    public void show_a_dialog_and_delete_a_recipe() {
        when(recipeRepository.delete(lauchkuchen)).thenReturn(CompletableFuture.completedFuture(null));

        onView(withId(R.id.action_details_delete)).perform(click());
        onView(withText(R.string.dialog_delete_recipe))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        verify(recipeRepository).delete(lauchkuchen);
    }

    @Test
    public void edit_recipe_and_update_ui() {
        Recipe editedLauchkuchen = RecipeTestUtil.createLauchkuchen();
        editedLauchkuchen.setTitle("Leckerster Lauchkuchen");
        editedLauchkuchen.setServings("1 Portion");
        editedLauchkuchen.setDirections("Einfach nur kochen und backen");

        Intent editIntent = new Intent();
        editIntent.putExtra(Recipe.class.getName(), editedLauchkuchen);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(RESULT_OK, editIntent);

        intending(hasComponent("org.flauschhaus.broccoli.recipe.crud.CreateAndEditRecipeActivity")).respondWith(result);

        onView(withId(R.id.action_details_edit)).perform(click());

        onView(withId(R.id.toolbar_layout)).check(matches(withContentDescription("Leckerster Lauchkuchen")));

        onView(withId(R.id.details_servings)).check(matches(withText("1 Portion")));

        onView(allOf(withId(R.id.direction_text), hasSibling(withText("1"))))
                .check(matches(withText("Einfach nur kochen und backen")));
        onView(allOf(withId(R.id.direction_text), hasSibling(withText("2"))))
                .check(doesNotExist());
    }

}