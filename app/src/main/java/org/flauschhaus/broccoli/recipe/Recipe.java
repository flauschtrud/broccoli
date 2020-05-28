package org.flauschhaus.broccoli.recipe;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import org.flauschhaus.broccoli.category.Category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {

    @Embedded
    public CoreRecipe coreRecipe = new CoreRecipe(); // TODO not public

    @Relation(
            parentColumn = "recipeId",
            entityColumn = "categoryId",
            associateBy = @Junction(RecipeCategoryAssociation.class)
    )
    public List<Category> categories = new ArrayList<>(); // TODO not public

    public long getRecipeId() {
        return coreRecipe.getRecipeId();
    }

    public void setRecipeId(long recipeId) {
        this.coreRecipe.setRecipeId(recipeId);
    }

    public String getTitle() {
        return coreRecipe.getTitle();
    }

    public void setTitle(String title) {
        this.coreRecipe.setTitle(title);
    }

    public String getImageName() {
        return coreRecipe.getImageName();
    }

    public void setImageName(String imageName) {
        this.coreRecipe.setImageName(imageName);
    }

    public String getDescription() {
        return coreRecipe.getDescription();
    }

    public void setDescription(String description) {
        this.coreRecipe.setDescription(description);
    }

    public String getServings() {
        return coreRecipe.getServings();
    }

    public void setServings(String servings) {
        this.coreRecipe.setServings(servings);
    }

    public String getPreparationTime() {
        return coreRecipe.getPreparationTime();
    }

    public void setPreparationTime(String preparationTime) {
        this.coreRecipe.setPreparationTime(preparationTime);
    }

    public String getSource() {
        return coreRecipe.getSource();
    }

    public void setSource(String source) {
        this.coreRecipe.setSource(source);
    }

    public String getIngredients() {
        return coreRecipe.getIngredients();
    }

    public void setIngredients(String ingredients) {
        this.coreRecipe.setIngredients(ingredients);
    }

    public String getDirections() {
        return coreRecipe.getDirections();
    }

    public void setDirections(String directions) {
        this.coreRecipe.setDirections(directions);
    }

    public boolean isDirty() {
        return coreRecipe.isDirty(); //TODO really? reicht eigentlich hier
    }

    public void setDirty(boolean dirty) {
        this.coreRecipe.setDirty(dirty); //TODO really?
    }
}
