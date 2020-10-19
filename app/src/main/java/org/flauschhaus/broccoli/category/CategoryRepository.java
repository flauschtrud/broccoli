package org.flauschhaus.broccoli.category;

import androidx.lifecycle.LiveData;

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

    @Inject
    CategoryRepository(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
        allCategories = categoryDAO.findAll();
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
}
