package com.flauschcode.broccoli.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {

    private LiveData<List<Category>> categories;
    private CategoryRepository categoryRepository;

    @Inject
    CategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        categories = categoryRepository.findAll();
    }

    LiveData<List<Category>> getCategories() {
        return categories;
    }

    void insertOrUpdate(Category category) {
        categoryRepository.insertOrUpdate(category);
    }

    public void delete(Category category) {
        categoryRepository.delete(category);
    }
}