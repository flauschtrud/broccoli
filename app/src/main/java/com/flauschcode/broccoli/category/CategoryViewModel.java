package com.flauschcode.broccoli.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.flauschcode.broccoli.support.BillingService;

import java.util.List;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {

    private LiveData<List<Category>> categories;

    private CategoryRepository categoryRepository;
    private BillingService billingService;

    @Inject
    CategoryViewModel(CategoryRepository categoryRepository, BillingService billingService) {
        this.categoryRepository = categoryRepository;
        categories = categoryRepository.findAll();
        this.billingService = billingService;
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

    public LiveData<Boolean> isPremium() {
        return billingService.isPremium();
    }
}