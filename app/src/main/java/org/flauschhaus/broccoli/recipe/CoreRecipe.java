package org.flauschhaus.broccoli.recipe;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "recipes")
public class CoreRecipe extends BaseObservable implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long recipeId = 0;
    private String title = "";
    private String imageName = "";
    private String description = "";
    private String servings = "";
    private String preparationTime = "";
    private String source = "";

    private String ingredients = "";
    private String directions = "";

    @Ignore
    private boolean isDirty = false;

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Bindable
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        notifyPropertyChanged(org.flauschhaus.broccoli.BR.imageName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreRecipe that = (CoreRecipe) o;
        return recipeId == that.recipeId &&
                isDirty == that.isDirty &&
                Objects.equals(title, that.title) &&
                Objects.equals(imageName, that.imageName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(servings, that.servings) &&
                Objects.equals(preparationTime, that.preparationTime) &&
                Objects.equals(source, that.source) &&
                Objects.equals(ingredients, that.ingredients) &&
                Objects.equals(directions, that.directions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, title, imageName, description, servings, preparationTime, source, ingredients, directions, isDirty);
    }
}
