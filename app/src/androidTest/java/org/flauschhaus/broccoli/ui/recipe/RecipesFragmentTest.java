package org.flauschhaus.broccoli.ui.recipe;

import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.DaggerMockApplicationComponent;
import org.flauschhaus.broccoli.MockApplicationComponent;
import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.flauschhaus.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import org.flauschhaus.broccoli.recipe.details.RecipeDetailsActivity;
import org.flauschhaus.broccoli.recipe.list.RecipesFragment;
import org.flauschhaus.broccoli.util.RecipeTestUtil;
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
import static org.flauschhaus.broccoli.util.RecyclerViewAssertions.hasItemsCount;
import static org.flauschhaus.broccoli.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class RecipesFragmentTest {

    @Inject
    RecipeRepository recipeRepository;

    private final Recipe lauchkuchen = RecipeTestUtil.createLauchkuchen();
    private final Recipe nusskuchen = RecipeTestUtil.createNusskuchen();

    @Before
    public void setUp() {
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        List<Recipe> recipes = new ArrayList<>();
        recipes.add(lauchkuchen);
        recipes.add(nusskuchen);
        when(recipeRepository.findAll()).thenReturn(new MutableLiveData<>(recipes));

        Intents.init();
        launchInContainer(RecipesFragment.class);
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
        onView(withId(R.id.fab)).perform(click());
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