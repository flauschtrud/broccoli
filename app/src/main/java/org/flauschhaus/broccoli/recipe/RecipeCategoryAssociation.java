package org.flauschhaus.broccoli.recipe;

import androidx.room.Entity;

@Entity(tableName = "recipes_with_categories", primaryKeys = {"recipeId", "categoryId"})
public class RecipeCategoryAssociation {
    public long recipeId;
    public long categoryId;

    public RecipeCategoryAssociation(long recipeId, long categoryId) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
    }

}
