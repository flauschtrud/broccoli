package com.flauschcode.broccoli.category;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.backup.autoexport.AutoExportScheduler;

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
    private final AutoExportScheduler autoExportScheduler;

    private final Category categoryAll;
    private final Category categoryFavorites;
    private final Category categoryUnassigned;
    private final Category categorySeasonal;

    @Inject
    CategoryRepository(Application application, CategoryDAO categoryDAO, AutoExportScheduler autoExportScheduler) {
        this.categoryDAO = categoryDAO;
        this.autoExportScheduler = autoExportScheduler;

        allCategories = categoryDAO.findAll();
        categoryAll = new Category(-1, application.getString(R.string.all_recipes));
        categoryFavorites = new Category(-2, application.getString(R.string.favorites));
        categoryUnassigned = new Category(-3, application.getString(R.string.unassigned) );
        categorySeasonal = new Category(-4, application.getString(R.string.seasonal_recipes));
    }

    public LiveData<List<Category>> findAll() {
        return allCategories;
    }

    public CompletableFuture<Void> delete(Category category) {
        return CompletableFuture.runAsync(() -> categoryDAO.delete(category))
                .thenRun(autoExportScheduler::scheduleIfEnabled);
    }

    public CompletableFuture<Void> insertOrUpdate(Category category) {
        CompletableFuture<Void> future = category.getCategoryId() == 0
                ? CompletableFuture.runAsync(() -> categoryDAO.insert(category))
                : CompletableFuture.runAsync(() -> categoryDAO.update(category));
        return future.thenRun(autoExportScheduler::scheduleIfEnabled);
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
