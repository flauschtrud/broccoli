package com.flauschcode.broccoli.category;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.util.RecyclerViewAssertions;
import com.flauschcode.broccoli.util.RecyclerViewMatcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

@RunWith(AndroidJUnit4.class)
public class CategoryFragmentTest {

    @Inject
    CategoryRepository categoryRepository;

    private final ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

    @Before
    public void setUp() {
        AccessibilityChecks.enable();

        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Blupp"));
        categories.add(new Category(2, "Lala"));
        when(categoryRepository.findAll()).thenReturn(new MutableLiveData<>(categories));

        launchInContainer(CategoryFragment.class, new Bundle(), com.google.android.material.R.style.Theme_AppCompat);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void add_new_category() {
        when(categoryRepository.insertOrUpdate(categoryCaptor.capture())).thenReturn(CompletableFuture.completedFuture(null));

        onView(withId(R.id.fab_categories)).perform(click());
        onView(withId(R.id.category_name)).perform(typeText("Mimi"));
        onView(withText(R.string.save_action))
                .inRoot(isDialog())
                .perform(click());

        Category category = categoryCaptor.getValue();
        assertThat(category.getName(), is("Mimi"));
    }

    @Test
    public void edit_category() {
        when(categoryRepository.insertOrUpdate(categoryCaptor.capture())).thenReturn(CompletableFuture.completedFuture(null));

        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_category_name)).perform(click());
        onView(withId(R.id.category_name)).perform(typeText("iti"));
        onView(withText(R.string.save_action))
                .inRoot(isDialog())
                .perform(click());

        Category category = categoryCaptor.getValue();
        assertThat(category.getName(), is("Bluppiti"));
    }

    @Test
    public void delete_category() {
        doNothing().when(categoryRepository).delete(categoryCaptor.capture());

        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_category_name)).perform(click());
        onView(withText(R.string.delete_action))
                .inRoot(isDialog())
                .perform(click());
        onView(withId(R.id.delete_category_warning)).check(matches(isDisplayed()));
        onView(withText(R.string.delete_action))
                .inRoot(isDialog())
                .perform(click());

        Category category = categoryCaptor.getValue();
        assertThat(category.getName(), is("Blupp"));
    }

    @Test
    public void recipes_are_shown_in_list() {
        onView(withId(R.id.recycler_view)).check(RecyclerViewAssertions.hasItemsCount(2));

        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_category_name)).check(matches(withText("Blupp")));
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.card_text_view_category_name)).check(matches(withText("Lala")));
    }
}