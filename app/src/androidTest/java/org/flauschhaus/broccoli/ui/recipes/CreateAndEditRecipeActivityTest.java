package org.flauschhaus.broccoli.ui.recipes;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class CreateAndEditRecipeActivityTest {

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    RecipeImageService recipeImageService;

    private Uri uri =  mock(Uri.class); // TODO how to make @Mock work?

    private ActivityScenario<CreateAndEditRecipeActivity> scenario;

    private View decorView;
    private ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

    private static final Recipe LAUCHKUCHEN = RecipeTestUtil.createLauchkuchen();
    private static final Recipe LAUCHKUCHEN_SAVED = RecipeTestUtil.createdAlreadySavedLauchkuchen();

    @Before
    public void setUp() {
        // TODO find out how to get a JUnit role working
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        Intents.init();
        scenario = launch(CreateAndEditRecipeActivity.class);
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
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.INSERT));
        when(recipeImageService.moveImage("12345.jpg")).thenReturn(CompletableFuture.completedFuture(null));

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));

        onView(withId(R.id.new_image)).perform(click());
        onView(withText(R.string.take_photo)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText(LAUCHKUCHEN.getTitle()));
        onView(withId(R.id.new_description)).perform(typeText(LAUCHKUCHEN.getDescription()));
        onView(withId(R.id.new_source)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getSource()));
        onView(withId(R.id.new_servings)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getServings()));
        onView(withId(R.id.new_preparation_time)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getPreparationTime()));
        onView(withId(R.id.new_ingredients)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getIngredients())); // it seems not to be possible to make Espresso type the enter key in a deterministic way
        onView(withId(android.R.id.content)).perform(swipeUp()); // scrollTo() does not work for NestedScrollViews
        onView(withId(R.id.new_directions)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getDirections()));
        onView(withId(R.id.button_save_recipe)).perform(click()); // TODO find out why there sometimes is such a long wait

        verify(recipeImageService).moveImage("12345.jpg");

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
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.UPDATE));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), LAUCHKUCHEN_SAVED);
        scenario = launch(intent);

        onView(withId(R.id.new_title)).check(matches(withText(LAUCHKUCHEN_SAVED.getTitle())));
        onView(withId(R.id.new_description)).check(matches(withText(LAUCHKUCHEN_SAVED.getDescription())));
        onView(withId(R.id.new_source)).check(matches(withText(LAUCHKUCHEN_SAVED.getSource())));
        onView(withId(R.id.new_servings)).check(matches(withText(LAUCHKUCHEN_SAVED.getServings())));
        onView(withId(R.id.new_preparation_time)).check(matches(withText(LAUCHKUCHEN_SAVED.getPreparationTime())));
        onView(withId(R.id.new_ingredients)).check(matches(withText(LAUCHKUCHEN_SAVED.getIngredients())));
        onView(withId(R.id.new_directions)).check(matches(withText(LAUCHKUCHEN_SAVED.getDirections())));

        onView(withId(R.id.new_servings)).perform(replaceText("1 Portion"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        Instrumentation.ActivityResult result = scenario.getResult();
        assertThat(result.getResultCode(), is(RESULT_OK));
        assertThat(result.getResultData().hasExtra(Recipe.class.getName()), is(true));

        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getId(), is(LAUCHKUCHEN_SAVED.getId()));
        assertThat(savedRecipe.getServings(), is("1 Portion"));

        Recipe editedRecipe = (Recipe) result.getResultData().getSerializableExtra(Recipe.class.getName());
        assertThat(editedRecipe.getId(), is(LAUCHKUCHEN_SAVED.getId()));
        assertThat(editedRecipe.getServings(), is("1 Portion"));
    }

    @Test
    public void remove_image(){
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.UPDATE));
        when(recipeImageService.deleteImage(LAUCHKUCHEN_SAVED.getImageName())).thenReturn(CompletableFuture.completedFuture(null));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), LAUCHKUCHEN_SAVED);
        scenario = launch(intent);

        onView(withId(R.id.new_image)).perform(click());
        onView(withText(R.string.remove_photo)).perform(click());
        onView(withId(R.id.button_save_recipe)).perform(click());

        Instrumentation.ActivityResult result = scenario.getResult();
        assertThat(result.getResultCode(), is(RESULT_OK));
        assertThat(result.getResultData().hasExtra(Recipe.class.getName()), is(true));

        verify(recipeImageService).deleteImage(LAUCHKUCHEN_SAVED.getImageName());

        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getId(), is(LAUCHKUCHEN_SAVED.getId()));
        assertThat(savedRecipe.getImageName(), is(""));
    }

    @Test
    public void replace_image() throws IOException {
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.UPDATE));
        when(recipeImageService.deleteImage(LAUCHKUCHEN_SAVED.getImageName())).thenReturn(CompletableFuture.completedFuture(null));

        when(recipeImageService.createTemporaryImage()).thenReturn(uri);
        when(uri.getLastPathSegment()).thenReturn("12345.jpg");
        when(recipeImageService.moveImage("12345.jpg")).thenReturn(CompletableFuture.completedFuture(null));

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), LAUCHKUCHEN_SAVED);
        scenario = launch(intent);

        onView(withId(R.id.new_image)).perform(click());
        onView(withText(R.string.take_photo)).perform(click());
        onView(withId(R.id.button_save_recipe)).perform(click());

        Instrumentation.ActivityResult result = scenario.getResult();
        assertThat(result.getResultCode(), is(RESULT_OK));
        assertThat(result.getResultData().hasExtra(Recipe.class.getName()), is(true));

        verify(recipeImageService).deleteImage(LAUCHKUCHEN_SAVED.getImageName());
        verify(recipeImageService).moveImage("12345.jpg");

        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getId(), is(LAUCHKUCHEN_SAVED.getId()));
        assertThat(savedRecipe.getImageName(), startsWith("12345.jpg"));
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
        onView(allOf(withClassName(endsWith("ImageButton")), withParent(withId(R.id.toolbar)))).perform(click());
        onView(withText(R.string.dialog_discard_changes))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    @Test
    public void pick_new_recipe() {
        when(recipeImageService.copyImage(uri)).thenReturn(CompletableFuture.completedFuture("12345.jpg"));
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.INSERT));
        when(recipeImageService.moveImage("12345.jpg")).thenReturn(CompletableFuture.completedFuture(null));

        Intent intent = new Intent();
        intent.setData(uri);
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, intent));

        onView(withId(R.id.new_image)).perform(click());
        onView(withText(R.string.pick_photo)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText(LAUCHKUCHEN.getTitle()));
        onView(withId(R.id.button_save_recipe)).perform(click()); // TODO find out why there sometimes is such a long wait

        verify(recipeImageService).moveImage("12345.jpg");

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe.getTitle(), is(LAUCHKUCHEN.getTitle()));
        assertThat(recipe.getImageName(), startsWith("12345.jpg"));
    }

}