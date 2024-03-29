package com.flauschcode.broccoli.recipe.cooking;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.ViewPagerIdlingResource;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.util.RecipeTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

@RunWith(AndroidJUnit4.class)
public class CookingAssistantActivityTest {

    @Inject
    PageableRecipeBuilder pageableRecipeBuilder;

    private ActivityScenario<CookingAssistantActivity> scenario;

    private static final Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();

    private final ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
    private final ArgumentCaptor<Float> scaleFactorCaptor = ArgumentCaptor.forClass(Float.class);

    private ViewPagerIdlingResource viewPagerIdlingResource;

    @Before
    public void setUp() {
        // does not work anymore after theme migration
        try {
            AccessibilityChecks.disable();
        } catch (IllegalStateException e) {
            // we can't disable multiple times
        }

        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());
    }

    private void givenScenarioWithLauchkuchen() {
        PageableRecipe pageableRecipe = new PageableRecipe();
        pageableRecipe.addPage(new PageableRecipe.Page("Ingredients", "100g Mehl\n50g Margarine"));
        pageableRecipe.addPage(new PageableRecipe.Page("1", "Erst dies."));
        pageableRecipe.addPage(new PageableRecipe.Page("2", "Dann das."));

        when(pageableRecipeBuilder.scale(scaleFactorCaptor.capture())).thenReturn(pageableRecipeBuilder);
        when(pageableRecipeBuilder.from(recipeCaptor.capture())).thenReturn(pageableRecipe);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CookingAssistantActivity.class);
        intent.putExtra(Recipe.class.getName(), lauchkuchen);
        scenario = launch(intent);

        scenario.onActivity(activity -> {
            viewPagerIdlingResource = new ViewPagerIdlingResource(activity.findViewById(R.id.cooking_assistant_pager), "ViewPagerInteractions");
            IdlingRegistry.getInstance().register(viewPagerIdlingResource);
        });
    }

    @After
    public void tearDown() {
        scenario.close();
        IdlingRegistry.getInstance().unregister(viewPagerIdlingResource);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void read_pages_via_swiping() {
        givenScenarioWithLauchkuchen();

        onView(withId(R.id.cooking_assistant_title)).check(matches(withText("Ingredients")));
        onView(withId(R.id.cooking_assistant_text)).check(matches(withText("100g Mehl\n50g Margarine")));

        onView(withId(R.id.cooking_assistant_pager)).perform(swipeLeft());
        onView(allOf(withId(R.id.cooking_assistant_title), isDisplayingAtLeast(60))).check(matches(withText("1")));
        onView(allOf(withId(R.id.cooking_assistant_text), isDisplayingAtLeast(60))).check(matches(withText("Erst dies.")));

        onView(withId(R.id.cooking_assistant_pager)).perform(swipeLeft());
        onView(allOf(withId(R.id.cooking_assistant_title), isDisplayingAtLeast(60))).check(matches(withText("2")));
        onView(allOf(withId(R.id.cooking_assistant_text), isDisplayingAtLeast(60))).check(matches(withText("Dann das.")));

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe, is(lauchkuchen));
    }

    @Test
    public void read_pages_via_seeking() {
        givenScenarioWithLauchkuchen();

        onView(allOf(withId(R.id.cooking_assistant_control), withContentDescription("2"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.cooking_assistant_title), isDisplayed())).check(matches(withText("2")));
        onView(allOf(withId(R.id.cooking_assistant_text), isDisplayed())).check(matches(withText("Dann das.")));

        onView(allOf(withId(R.id.cooking_assistant_control), withContentDescription("1"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.cooking_assistant_title), isDisplayed())).check(matches(withText("1")));
        onView(allOf(withId(R.id.cooking_assistant_text), isDisplayed())).check(matches(withText("Erst dies.")));

        onView(allOf(withId(R.id.cooking_assistant_control), withContentDescription("0"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.cooking_assistant_title), isDisplayed())).check(matches(withText("Ingredients")));
        onView(allOf(withId(R.id.cooking_assistant_text), isDisplayed())).check(matches(withText("100g Mehl\n50g Margarine")));

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe, is(lauchkuchen));
    }

    @Test
    public void scale_simple_mode() {
        givenScenarioWithLauchkuchen();

        onView(withId(R.id.fullscreen_layout)).perform(click());
        onView(withId(R.id.fullscreen_layout)).perform(click()); // scaling button is not visible for Espresso otherwise

        onView(withId(R.id.button_scaling)).perform(click());

        onView(withText(R.string.adjust_ingredients))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.number_of_servings))
                .inRoot(isDialog())
                .perform(clearText(), typeText("2"));
        onView(withId(android.R.id.button1)).perform(click());

        Float scaleFactor = scaleFactorCaptor.getValue();
        assertThat(scaleFactor, is(0.5f));
    }

    @Test
    public void scale_pro_mode() {
        givenScenarioWithLauchkuchen();

        onView(withId(R.id.fullscreen_layout)).perform(click());
        onView(withId(R.id.fullscreen_layout)).perform(click()); // scaling button is not visible for Espresso otherwise

        onView(withId(R.id.button_scaling)).perform(click());

        onView(withText(R.string.adjust_ingredients))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.button_pro_scaling))
                .inRoot(isDialog())
                .perform(click());

        onView(withId(R.id.scale_factor))
                .inRoot(isDialog())
                .perform(clearText(), typeText("2"));
        onView(withId(android.R.id.button1)).perform(click());

        Float scaleFactor = scaleFactorCaptor.getValue();
        assertThat(scaleFactor, is(2.0f));
    }

    @Test
    public void scale_with_missing_input() {
        givenScenarioWithLauchkuchen();

        onView(withId(R.id.fullscreen_layout)).perform(click());
        onView(withId(R.id.fullscreen_layout)).perform(click()); // scaling button is not visible for Espresso otherwise

        onView(withId(R.id.button_scaling)).perform(click());

        onView(withText(R.string.adjust_ingredients))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.number_of_servings))
                .inRoot(isDialog())
                .perform(clearText());
        onView(withId(android.R.id.button1)).perform(click());

        verify(pageableRecipeBuilder).from(recipeCaptor.capture());
        verifyNoMoreInteractions(pageableRecipeBuilder);
    }

    @Test
    public void load_empty_page() { // https://github.com/flauschtrud/broccoli/issues/176
        PageableRecipe pageableRecipe = new PageableRecipe();
        pageableRecipe.addPage(new PageableRecipe.Page("Ingredients", ""));

        when(pageableRecipeBuilder.scale(anyFloat())).thenReturn(pageableRecipeBuilder);
        when(pageableRecipeBuilder.from(any(Recipe.class))).thenReturn(pageableRecipe);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CookingAssistantActivity.class);
        intent.putExtra(Recipe.class.getName(), new Recipe());
        scenario = launch(intent);

        onView(withId(R.id.cooking_assistant_title)).check(matches(withText("Ingredients")));
        onView(withId(R.id.cooking_assistant_text)).check(matches(withText("")));
    }

}