package com.flauschcode.broccoli.category;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CategoryViewModelTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LiveData<List<Category>> liveData;

    private CategoryViewModel categoryViewModel;

    @Before
    public void setUp() {
        when(categoryRepository.findAll()).thenReturn(liveData);
        categoryViewModel = new CategoryViewModel(categoryRepository);
    }

    @Test
    public void get_all_categories() {
        assertThat(categoryViewModel.getCategories(), is(liveData));
    }

    @Test
    public void insert_or_update_category() {
        Category category = new Category("blupp");
        categoryViewModel.insertOrUpdate(category);
        verify(categoryRepository).insertOrUpdate(category);
    }

    @Test
    public void delete_category() {
        Category category = new Category("blupp");
        categoryViewModel.delete(category);
        verify(categoryRepository).delete(category);
    }

}