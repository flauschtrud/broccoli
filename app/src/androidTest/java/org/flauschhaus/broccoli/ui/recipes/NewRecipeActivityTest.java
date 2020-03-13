package org.flauschhaus.broccoli.ui.recipes;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.DaggerMockApplicationComponent;
import org.flauschhaus.broccoli.MockApplicationComponent;
import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NewRecipeActivityTest {

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    RecipeImageService recipeImageService;

    private Uri uri =  mock(Uri.class); // TODO how to make @Mock work?

    private ActivityScenario<NewRecipeActivity> scenario;

    private View decorView;
    private ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

    private static final Recipe LAUCHKUCHEN = RecipeTestUtil.createLauchkuchen();

    @Before
    public void setUp() {
        // TODO find out how to get a JUnit role working
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        Intents.init();
        scenario = launch(NewRecipeActivity.class);
        scenario.onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @After
    public void tearDown() {
        scenario.close();
        Intents.release();
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void save_new_recipe() throws IOException {

        when(recipeImageService.createTemporaryImage()).thenReturn(uri);
        when(uri.getLastPathSegment()).thenReturn("12345.jpg");
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(null));
        when(recipeImageService.moveImage("12345.jpg")).thenReturn(CompletableFuture.completedFuture(null));

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));

        onView(withId(R.id.new_image)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText(LAUCHKUCHEN.getTitle()));
        onView(withId(R.id.new_description)).perform(typeText(LAUCHKUCHEN.getDescription()));
        onView(withId(R.id.new_source)).perform(typeText(LAUCHKUCHEN.getSource()));
        onView(withId(R.id.new_servings)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getServings()));
        onView(withId(R.id.new_preparation_time)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getPreparationTime()));
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

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe.getTitle(), is(LAUCHKUCHEN.getTitle()));
        assertThat(recipe.getDescription(), is(LAUCHKUCHEN.getDescription()));
        assertThat(recipe.getSource(), is(LAUCHKUCHEN.getSource()));
        assertThat(recipe.getServings(), is(LAUCHKUCHEN.getServings()));
        assertThat(recipe.getPreparationTime(), is(LAUCHKUCHEN.getPreparationTime()));
        assertThat(recipe.getIngredients(), is(LAUCHKUCHEN.getIngredients()));
        assertThat(recipe.getDirections(), is(LAUCHKUCHEN.getDirections()));
        assertThat(recipe.getImageName(), startsWith("12345.jpg"));
    }

    @Test
    public void edit_recipe(){
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(null));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), NewRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), LAUCHKUCHEN);
        scenario = launch(intent);

        onView(withId(R.id.new_title)).check(matches(withText(LAUCHKUCHEN.getTitle())));
        onView(withId(R.id.new_description)).check(matches(withText(LAUCHKUCHEN.getDescription())));
        onView(withId(R.id.new_source)).check(matches(withText(LAUCHKUCHEN.getSource())));
        onView(withId(R.id.new_servings)).check(matches(withText(LAUCHKUCHEN.getServings())));
        onView(withId(R.id.new_preparation_time)).check(matches(withText(LAUCHKUCHEN.getPreparationTime())));
        onView(withId(R.id.new_ingredients)).check(matches(withText(LAUCHKUCHEN.getIngredients())));
        onView(withId(R.id.new_directions)).check(matches(withText(LAUCHKUCHEN.getDirections())));

        onView(withId(R.id.new_servings)).perform(replaceText("1 Portion"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        Instrumentation.ActivityResult result = scenario.getResult();
        assertThat(result.getResultCode(), is(RESULT_OK));
        assertThat(result.getResultData().hasExtra(Recipe.class.getName()), is(true));

        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getId(), is(LAUCHKUCHEN.getId()));
        assertThat(savedRecipe.getServings(), is("1 Portion"));

        Recipe editedRecipe = (Recipe) result.getResultData().getSerializableExtra(Recipe.class.getName());
        assertThat(editedRecipe.getId(), is(LAUCHKUCHEN.getId()));
        assertThat(editedRecipe.getServings(), is("1 Portion"));
    }

    @Test
    public void do_not_save_when_title_is_empty() {
        onView(withId(R.id.new_title)).perform(typeText("       "));
        onView(withId(R.id.button_save_recipe)).perform(click());

        onView(withText(R.string.toast_title_is_empty))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        verifyNoMoreInteractions(recipeRepository);
    }

    @Test
    public void show_warning_on_cancel_when_recipe_is_dirty() {
        onView(withId(R.id.new_title)).perform(typeText("Mjam mjam"));
        onView(allOf(withContentDescription(R.string.toolbar_navigate_up), isClickable())).perform(click());
        onView(withText(R.string.dialog_discard_changes))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

}