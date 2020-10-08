package org.flauschhaus.broccoli.category;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.R;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "categories")
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long categoryId = 0;
    private String name = "";

    public Category() {}

    @Ignore
    public Category(String name) {
        this.name = name;
    }

    public Category(long categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryId == category.categoryId &&
                Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, name);
    }

    @Override
    public String toString() {
        return name;
    }

    public static final Category ALL = new Category(-1, BroccoliApplication.getContext() != null? BroccoliApplication.getContext().getString(R.string.all_recipes) : "All recipes");
    public static final Category FAVORITES = new Category(-2, BroccoliApplication.getContext() != null? BroccoliApplication.getContext().getString(R.string.favorites) : "Favorites");
    public static final Category UNASSIGNED = new Category(-3, BroccoliApplication.getContext() != null? BroccoliApplication.getContext().getString(R.string.unassigned) : "Unassigned");


}
