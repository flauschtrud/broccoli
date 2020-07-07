package org.flauschhaus.broccoli.recipe.cooking;

import android.content.Intent;
import android.view.View;
import android.widget.SeekBar;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.DaggerMockApplicationComponent;
import org.flauschhaus.broccoli.MockApplicationComponent;
import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.details.RecipeDetailsActivity;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class CookingModeActivityTest {

    @Inject
    PageableRecipeBuilder pageableRecipeBuilder;

    private ActivityScenario<RecipeDetailsActivity> scenario;

    private static Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();
    private ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

    @Before
    public void setUp() {
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        PageableRecipe pageableRecipe = new PageableRecipe();
        pageableRecipe.addPage(new PageableRecipe.Page("Ingredients", "100g Mehl\n50g Margarine"));
        pageableRecipe.addPage(new PageableRecipe.Page("1", "Erst dies."));
        pageableRecipe.addPage(new PageableRecipe.Page("2", "Dann das."));

        when(pageableRecipeBuilder.from(recipeCaptor.capture())).thenReturn(pageableRecipe);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CookingModeActivity.class);
        intent.putExtra(Recipe.class.getName(), lauchkuchen);
        scenario = launch(intent);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void read_pages_via_swiping() {
        onView(withId(R.id.cooking_mode_title)).check(matches(withText("Ingredients")));
        onView(withId(R.id.cooking_mode_text)).check(matches(withText("100g Mehl\n50g Margarine")));

        onView(withId(R.id.cooking_mode_pager)).perform(swipeLeft());
        onView(allOf(withId(R.id.cooking_mode_title), isDisplayingAtLeast(60))).check(matches(withText("1")));
        onView(allOf(withId(R.id.cooking_mode_text), isDisplayingAtLeast(60))).check(matches(withText("Erst dies.")));

        onView(withId(R.id.cooking_mode_pager)).perform(swipeLeft());
        onView(allOf(withId(R.id.cooking_mode_title), isDisplayingAtLeast(60))).check(matches(withText("2")));
        onView(allOf(withId(R.id.cooking_mode_text), isDisplayingAtLeast(60))).check(matches(withText("Dann das.")));

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe, is(lauchkuchen));
    }

    @Test
    public void read_pages_via_seeking() {
        onView(withId(R.id.cooking_mode_seekbar)).perform(setProgress(2));
        onView(allOf(withId(R.id.cooking_mode_title), isDisplayingAtLeast(60))).check(matches(withText("2")));
        onView(allOf(withId(R.id.cooking_mode_text), isDisplayingAtLeast(60))).check(matches(withText("Dann das.")));

        onView(withId(R.id.cooking_mode_seekbar)).perform(setProgress(1));
        onView(allOf(withId(R.id.cooking_mode_title), isDisplayingAtLeast(60))).check(matches(withText("1")));
        onView(allOf(withId(R.id.cooking_mode_text), isDisplayingAtLeast(60))).check(matches(withText("Erst dies.")));

        onView(withId(R.id.cooking_mode_seekbar)).perform(setProgress(0));
        onView(withId(R.id.cooking_mode_title)).check(matches(withText("Ingredients")));
        onView(withId(R.id.cooking_mode_text)).check(matches(withText("100g Mehl\n50g Margarine")));

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe, is(lauchkuchen));
    }

    static ViewAction setProgress(final int progress) {

        return new ViewAction() {

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

        };

    }

}