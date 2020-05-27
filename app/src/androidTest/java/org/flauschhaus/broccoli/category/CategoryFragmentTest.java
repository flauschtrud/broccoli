package org.flauschhaus.broccoli.category;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.DaggerMockApplicationComponent;
import org.flauschhaus.broccoli.MockApplicationComponent;
import org.flauschhaus.broccoli.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.flauschhaus.broccoli.util.RecyclerViewAssertions.hasItemsCount;
import static org.flauschhaus.broccoli.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class CategoryFragmentTest {

    @Inject
    CategoryRepository categoryRepository;

    private ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

    @Before
    public void setUp() {
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Blupp"));
        categories.add(new Category("Lala"));
        when(categoryRepository.findAll()).thenReturn(new MutableLiveData<>(categories));

        launchInContainer(CategoryFragment.class, null, R.style.Theme_AppCompat, null);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void add_new_category() {
        doNothing().when(categoryRepository).insert(categoryCaptor.capture());

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.new_category_name)).perform(typeText("Mimi"));
        onView(withText(R.string.action_save))
                .inRoot(isDialog())
                .perform(click());

        Category category = categoryCaptor.getValue();
        assertThat(category.getName(), is("Mimi"));
    }

    @Test
    public void recipes_are_shown_in_list() {
        onView(withId(R.id.recycler_view)).check(hasItemsCount(2));

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_category_name)).check(matches(withText("Blupp")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.card_text_view_category_name)).check(matches(withText("Lala")));
    }
}