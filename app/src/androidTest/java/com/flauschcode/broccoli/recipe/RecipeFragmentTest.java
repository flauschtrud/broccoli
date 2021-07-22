package com.flauschcode.broccoli.recipe;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import com.flauschcode.broccoli.recipe.details.RecipeDetailsActivity;
import com.flauschcode.broccoli.recipe.list.RecipeFragment;
import com.flauschcode.broccoli.util.RecipeTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.flauschcode.broccoli.util.RecyclerViewAssertions.hasItemsCount;
import static com.flauschcode.broccoli.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class RecipeFragmentTest {

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    CategoryRepository categoryRepository;

    private final Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();
    private final Recipe nusskuchen = RecipeTestUtil.createNusskuchen();

    @Before
    public void setUp() {
        AccessibilityChecks.enable();

        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        List<Recipe> recipes = new ArrayList<>();
        recipes.add(lauchkuchen);
        recipes.add(nusskuchen);
        when(recipeRepository.find(any(RecipeRepository.SearchCriteria.class))).thenReturn(new MutableLiveData<>(recipes));

        when(categoryRepository.findAll()).thenReturn(new MutableLiveData<>(new ArrayList<>()));

        Intents.init();
        launchInContainer(RecipeFragment.class, new Bundle());
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void trigger_new_recipe_activity_when_fab_is_clicked() {
        onView(withId(R.id.fab_recipes)).perform(click());
        intended(hasComponent(CreateAndEditRecipeActivity.class.getName()));
    }

    @Test
    public void recipes_are_shown_in_list() {
        onView(withId(R.id.recycler_view)).check(hasItemsCount(2));

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText(lauchkuchen.getTitle())));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_description)).check(matches(withText(lauchkuchen.getDescription())));

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.card_text_view_title)).check(matches(withText(nusskuchen.getTitle())));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.card_text_view_description)).check(matches(withText(nusskuchen.getDescription())));
    }

    @Test
    public void show_details_of_selected_recipe() {
        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));

        intended(allOf(
                hasComponent(RecipeDetailsActivity.class.getName()),
                hasExtra(Recipe.class.getName(), lauchkuchen)
        ));
    }

}