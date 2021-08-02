package com.flauschcode.broccoli.category;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.flauschcode.broccoli.R;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CategoryRepository {

    private CategoryDAO categoryDAO;
    private LiveData<List<Category>> allCategories;

    private final Category categoryAll;
    private final Category categoryFavorites;
    private final Category categoryUnassigned;
    private final Category categorySeasonal;

    @Inject
    CategoryRepository(Application application, CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;

        allCategories = categoryDAO.findAll();
        categoryAll = new Category(-1, application.getString(R.string.all_recipes));
        categoryFavorites = new Category(-2, application.getString(R.string.favorites));
        categoryUnassigned = new Category(-3, application.getString(R.string.unassigned) );
        categorySeasonal = new Category(-4, application.getString(R.string.seasonal_recipes));
    }

    public LiveData<List<Category>> findAll() {
        return allCategories;
    }

    public void delete(Category category) {
        CompletableFuture.runAsync(() -> categoryDAO.delete(category));
    }

    public CompletableFuture<Void> insertOrUpdate(Category category) {
        if (category.getCategoryId() == 0) {
            return CompletableFuture.runAsync(() -> categoryDAO.insert(category));
        } else {
            return CompletableFuture.runAsync(() -> categoryDAO.update(category));
        }
    }

    public CompletableFuture<List<Category>> retainExisting(List<Category> categories) {
        return CompletableFuture.supplyAsync(() -> categories.stream()
                    .map(category -> categoryDAO.searchByName(category.getName()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
    }

    public CompletableFuture<List<Category>> retainNonExisting(List<Category> categories) {
        return CompletableFuture.supplyAsync(() -> categories.stream()
                .filter(category -> categoryDAO.searchByName(category.getName()) == null)
                .collect(Collectors.toList()));
    }

    public Category getAllRecipesCategory() {
        return categoryAll;
    }

    public Category getFavoritesCategory() {
        return categoryFavorites;
    }

    public Category getUnassignedRecipesCategory() {
        return categoryUnassigned;
    }

    public Category getSeasonalRecipesCategory() {
        return categorySeasonal;
    }
}
