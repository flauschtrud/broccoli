package com.flauschcode.broccoli.recipe;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.util.RecipeTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_SEND;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.core.app.ActivityScenario.launchActivityForResult;
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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

    @Inject
    CategoryRepository categoryRepository;

    private Uri uri =  mock(Uri.class); // TODO how to make @Mock work?

    private ActivityScenario<CreateAndEditRecipeActivity> scenario;

    private final ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

    private static final Recipe LAUCHKUCHEN = RecipeTestUtil.createLauchkuchen();
    private static final Recipe LAUCHKUCHEN_SAVED = RecipeTestUtil.createdAlreadySavedLauchkuchen();

    @Before
    public void setUp() {
        AccessibilityChecks.enable();

        // TODO find out how to get a JUnit role working
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        Intents.init();
        scenario = launch(CreateAndEditRecipeActivity.class);
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
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(1L));
        when(recipeImageService.moveImage("12345.jpg")).thenReturn(CompletableFuture.completedFuture(null));

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));

        List<Category> categories = new ArrayList<>();
        Category categoryHauptgerichte = new Category("Hauptgerichte");
        categories.add(categoryHauptgerichte);
        categories.add(new Category("Nachtisch"));
        when(categoryRepository.findAll()).thenReturn(new MutableLiveData<>(categories));

        onView(withId(R.id.new_image)).perform(click());
        onView(withText(R.string.take_photo)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText(LAUCHKUCHEN.getTitle()));

        onView(withId(R.id.new_categories)).perform(closeSoftKeyboard(), click());
        onView(withText("Hauptgerichte")).perform(click());
        onView(withText("Nachtisch")).perform(click());
        onView(withText("Nachtisch")).perform(click());
        onView(withText(android.R.string.ok)).perform(click());

        onView(withId(R.id.new_description)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getDescription()));
        onView(withId(R.id.new_source)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getSource()));
        onView(withId(R.id.new_servings)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getServings()));
        onView(withId(R.id.new_preparation_time)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getPreparationTime()));
        onView(withId(android.R.id.content)).perform(swipeUp()); // scrollTo() does not work for NestedScrollViews
        onView(withId(R.id.new_ingredients)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getIngredients())); // it seems not to be possible to make Espresso type the enter key in a deterministic way
        onView(withId(R.id.new_directions)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getDirections()));
        onView(withId(R.id.new_nutritional_values)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getNutritionalValues()));
        onView(withId(R.id.new_notes)).perform(closeSoftKeyboard(), typeText(LAUCHKUCHEN.getNotes()));

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
        assertThat(recipe.getNutritionalValues(), is(LAUCHKUCHEN.getNutritionalValues()));
        assertThat(recipe.getNotes(), is(LAUCHKUCHEN.getNotes()));
        assertThat(recipe.getImageName(), startsWith("12345.jpg"));
        assertThat(recipe.getCategories().size(), is(1));
        assertThat(recipe.getCategories().get(0), is(categoryHauptgerichte));

    }

    @Test
    public void edit_recipe(){
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(1L));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), LAUCHKUCHEN_SAVED);
        scenario = launchActivityForResult(intent);

        onView(withId(R.id.new_title)).check(matches(withText(LAUCHKUCHEN_SAVED.getTitle())));
        onView(withId(R.id.new_categories)).check(matches(withText(LAUCHKUCHEN_SAVED.getCategories().stream().map(Category::getName).collect(Collectors.joining(", ")))));
        onView(withId(R.id.new_description)).check(matches(withText(LAUCHKUCHEN_SAVED.getDescription())));
        onView(withId(R.id.new_source)).check(matches(withText(LAUCHKUCHEN_SAVED.getSource())));
        onView(withId(R.id.new_servings)).check(matches(withText(LAUCHKUCHEN_SAVED.getServings())));
        onView(withId(R.id.new_preparation_time)).check(matches(withText(LAUCHKUCHEN_SAVED.getPreparationTime())));
        onView(withId(R.id.new_ingredients)).check(matches(withText(LAUCHKUCHEN_SAVED.getIngredients())));
        onView(withId(R.id.new_directions)).check(matches(withText(LAUCHKUCHEN_SAVED.getDirections())));
        onView(withId(R.id.new_nutritional_values)).check(matches(withText(LAUCHKUCHEN_SAVED.getNutritionalValues())));
        onView(withId(R.id.new_notes)).check(matches(withText(LAUCHKUCHEN_SAVED.getNotes())));

        onView(withId(R.id.new_servings)).perform(replaceText("1 Portion"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        Instrumentation.ActivityResult result = scenario.getResult();
        assertThat(result.getResultCode(), is(RESULT_OK));
        assertThat(result.getResultData().hasExtra(Recipe.class.getName()), is(true));

        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getRecipeId(), is(LAUCHKUCHEN_SAVED.getRecipeId()));
        assertThat(savedRecipe.getServings(), is("1 Portion"));

        Recipe editedRecipe = (Recipe) result.getResultData().getSerializableExtra(Recipe.class.getName());
        assertThat(editedRecipe.getRecipeId(), is(LAUCHKUCHEN_SAVED.getRecipeId()));
        assertThat(editedRecipe.getServings(), is("1 Portion"));
    }

    @Test
    public void remove_image(){
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(1L));
        when(recipeImageService.deleteImage(LAUCHKUCHEN_SAVED.getImageName())).thenReturn(CompletableFuture.completedFuture(null));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), LAUCHKUCHEN_SAVED);
        scenario = launchActivityForResult(intent);

        onView(withId(R.id.new_image)).perform(click());
        onView(withText(R.string.remove_photo)).perform(click());
        onView(withId(R.id.button_save_recipe)).perform(click());

        Instrumentation.ActivityResult result = scenario.getResult();
        assertThat(result.getResultCode(), is(RESULT_OK));
        assertThat(result.getResultData().hasExtra(Recipe.class.getName()), is(true));

        verify(recipeImageService).deleteImage(LAUCHKUCHEN_SAVED.getImageName());

        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getRecipeId(), is(LAUCHKUCHEN_SAVED.getRecipeId()));
        assertThat(savedRecipe.getImageName(), is(""));
    }

    @Test
    public void replace_image() throws IOException {
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(1L));
        when(recipeImageService.deleteImage(LAUCHKUCHEN_SAVED.getImageName())).thenReturn(CompletableFuture.completedFuture(null));

        when(recipeImageService.createTemporaryImage()).thenReturn(uri);
        when(uri.getLastPathSegment()).thenReturn("12345.jpg");
        when(recipeImageService.moveImage("12345.jpg")).thenReturn(CompletableFuture.completedFuture(null));

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.putExtra(Recipe.class.getName(), LAUCHKUCHEN_SAVED);
        scenario = launchActivityForResult(intent);

        onView(withId(R.id.new_image)).perform(click());
        onView(withText(R.string.take_photo)).perform(click());
        onView(withId(R.id.button_save_recipe)).perform(click());

        Instrumentation.ActivityResult result = scenario.getResult();
        assertThat(result.getResultCode(), is(RESULT_OK));
        assertThat(result.getResultData().hasExtra(Recipe.class.getName()), is(true));

        verify(recipeImageService).deleteImage(LAUCHKUCHEN_SAVED.getImageName());
        verify(recipeImageService).moveImage("12345.jpg");

        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getRecipeId(), is(LAUCHKUCHEN_SAVED.getRecipeId()));
        assertThat(savedRecipe.getImageName(), startsWith("12345.jpg"));
    }

    @Test
    public void do_not_save_when_title_is_empty() {
        onView(withId(R.id.new_title)).perform(typeText("       "));
        onView(withId(R.id.button_save_recipe)).perform(click());

        // verifying displaying of the toast does not always work deterministically

        verifyNoMoreInteractions(recipeRepository);
    }

    @Test
    public void show_warning_on_cancel_when_recipe_is_dirty() {
        onView(withId(R.id.new_title)).perform(typeText("Mjam mjam"));
        onView(allOf(withClassName(endsWith("ImageButton")), withParent(withId(R.id.toolbar)))).perform(click());
        onView(withText(R.string.discard_changes_question))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    @Test
    public void pick_image() {
        when(recipeImageService.copyImage(uri)).thenReturn(CompletableFuture.completedFuture("12345.jpg"));
        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(1L));
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

    @Test
    public void should_not_break_if_url_is_not_available() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, (String) null);

        scenario = launchActivityForResult(intent);

        onView(withId(R.id.new_title)).check(matches(withText("")));
    }

}