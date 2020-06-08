package org.flauschhaus.broccoli.recipe;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import org.flauschhaus.broccoli.category.Category;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "recipes_with_categories",
        primaryKeys = {
                "recipeId", "categoryId"
        },
        foreignKeys = {
                @ForeignKey(
                        entity = CoreRecipe.class,
                        parentColumns = "recipeId",
                        childColumns = "recipeId",
                        onDelete = CASCADE),
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "categoryId",
                        childColumns = "categoryId",
                        onDelete = CASCADE)
        },
        indices = {
                @Index("recipeId"),
                @Index("categoryId")
        })
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
