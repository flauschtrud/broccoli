package com.flauschcode.broccoli.recipe;

import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.cooking.CookingAssistantActivity;
import com.flauschcode.broccoli.recipe.details.RecipeDetailsActivity;
import com.flauschcode.broccoli.recipe.sharing.ShareRecipeAsFileService;
import com.flauschcode.broccoli.recipe.sharing.ShareableRecipe;
import com.flauschcode.broccoli.recipe.sharing.ShareableRecipeBuilder;
import com.flauschcode.broccoli.util.RecipeTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    ShareableRecipeBuilder shareableRecipeBuilder;

    @Inject
    ShareRecipeAsFileService shareRecipeAsFileService;

    private ActivityScenario<RecipeDetailsActivity> scenario;

    private static Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();
    private ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

    @Before
    public void setUp() {
        AccessibilityChecks.enable();

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
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void display_all_the_data_of_a_recipe() {
        onView(withId(R.id.details_title)).check(matches(withText(lauchkuchen.getTitle())));
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

        onView(withId(R.id.action_details_like)).check(matches(isDisplayed()));

        lauchkuchen.getCategories().forEach(category -> onView(withId(R.id.details_categories)).check(matches(withSubstring(category.getName()))));
    }

    @Test
    public void toggle_favorite() {
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.UPDATE));
        onView(withId(R.id.action_details_like)).perform(click());
        onView(withId(R.id.action_details_unlike)).check(matches(isDisplayed()));

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe.getTitle(), is(lauchkuchen.getTitle()));
        assertThat(recipe.isFavorite(), is(true));
    }

    @Test
    public void share() {
        Uri imageUri = Uri.parse("https://www.blablupp.com/image");
        when(shareableRecipeBuilder.from(recipeCaptor.capture())).thenReturn(new ShareableRecipe("Lauchkuchen in plain text.", imageUri));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.share_action)).perform(click());

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe.getTitle(), is(lauchkuchen.getTitle()));

        intended(allOf(hasAction(Intent.ACTION_CHOOSER),
                hasExtra(is(Intent.EXTRA_INTENT),
                        allOf( hasAction(Intent.ACTION_SEND),
                                hasExtra(Intent.EXTRA_SUBJECT, lauchkuchen.getTitle()),
                                hasExtra(Intent.EXTRA_TEXT, "Lauchkuchen in plain text."),
                                hasExtra(Intent.EXTRA_STREAM, imageUri),
                                hasType("text/plain"),
                                hasFlag(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        ))));
    }

    @Test
    public void share_as_file() throws IOException {
        Uri recipeUri = Uri.parse("content://bla");
        when(shareRecipeAsFileService.shareAsFile(recipeCaptor.capture())).thenReturn(recipeUri);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.share_as_file_action)).perform(click());

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe.getTitle(), is(lauchkuchen.getTitle()));

        intended(allOf(hasAction(Intent.ACTION_CHOOSER),
                hasExtra(is(Intent.EXTRA_INTENT),
                        allOf( hasAction(Intent.ACTION_SEND),
                                hasExtra(Intent.EXTRA_STREAM, recipeUri),
                                hasType("application/broccoli"),
                                hasFlag(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        ))));
    }

    @Test
    public void cook() {
        onView(withId(R.id.fab_cooking_assistant)).perform(click());

        intended(allOf(
                hasComponent(CookingAssistantActivity.class.getName()),
                hasExtra(Recipe.class.getName(), lauchkuchen)
        ));
    }

    @Test
    public void show_a_dialog_and_delete_a_recipe() {
        when(recipeRepository.delete(lauchkuchen)).thenReturn(CompletableFuture.completedFuture(null));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.delete_action)).perform(click());

        onView(withText(R.string.delete_recipe_question))
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

        intending(hasComponent("com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity")).respondWith(result);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.edit_action)).perform(click());

        onView(withId(R.id.details_title)).check(matches(withText("Leckerster Lauchkuchen")));
        onView(withId(R.id.details_servings)).check(matches(withText("1 Portion")));

        onView(allOf(withId(R.id.direction_text), hasSibling(withText("1"))))
                .check(matches(withText("Einfach nur kochen und backen")));
        onView(allOf(withId(R.id.direction_text), hasSibling(withText("2"))))
                .check(doesNotExist());
    }

}