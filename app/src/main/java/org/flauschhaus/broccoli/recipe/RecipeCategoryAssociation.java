package org.flauschhaus.broccoli.recipe;

import androidx.room.Entity;

@Entity(tableName = "recipes_with_categories", primaryKeys = {"recipeId", "categoryId"})
public class RecipeCategoryAssociation {
    private long recipeId;
    private long categoryId;

    public RecipeCategoryAssociation(long recipeId, long categoryId) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
