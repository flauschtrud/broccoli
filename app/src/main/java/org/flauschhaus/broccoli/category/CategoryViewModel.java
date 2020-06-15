package org.flauschhaus.broccoli.category;

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

    void add(Category category) {
        categoryRepository.insert(category);
    }

}