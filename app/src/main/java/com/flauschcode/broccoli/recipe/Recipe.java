package com.flauschcode.broccoli.recipe;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.category.Category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe extends BaseObservable implements Serializable {

    @Embedded
    private CoreRecipe coreRecipe = new CoreRecipe();

    @Relation(
            parentColumn = "recipeId",
            entityColumn = "categoryId",
            associateBy = @Junction(RecipeCategoryAssociation.class)
    )
    private List<Category> categories = new ArrayList<>();

    @Ignore
    private boolean isDirty = false;

    public Recipe() {}

    public Recipe(Recipe other) {
        this.coreRecipe = other.getCoreRecipe();
        this.categories.addAll(other.getCategories());
        this.setRecipeId(0);
    }

    @JsonIgnore
    public CoreRecipe getCoreRecipe() {
        return coreRecipe;
    }

    public void setCoreRecipe(CoreRecipe coreRecipe) {
        this.coreRecipe = coreRecipe;
    }

    @Bindable
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
        notifyPropertyChanged(BR.categories);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        notifyPropertyChanged(BR.categories);
    }

    @JsonIgnore
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

    @Bindable
    public String getImageName() {
        return coreRecipe.getImageName();
    }

    public void setImageName(String imageName) {
        this.coreRecipe.setImageName(imageName);
        notifyPropertyChanged(com.flauschcode.broccoli.BR.imageName);
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

    public String getNutritionalValues() {
        return coreRecipe.getNutritionalValues();
    }

    public void setNutritionalValues(String nutritionalValues) {
        this.coreRecipe.setNutritionalValues(nutritionalValues);
    }

    public String getNotes() {
        return coreRecipe.getNotes();
    }

    public void setNotes(String notes) {
        this.coreRecipe.setNotes(notes);
    }

    public boolean isFavorite() {
        return coreRecipe.isFavorite();
    }

    public void setFavorite(boolean favorite) {
        this.coreRecipe.setFavorite(favorite);
    }

    @JsonIgnore
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
        Recipe recipe = (Recipe) o;
        return Objects.equals(coreRecipe, recipe.coreRecipe) &&
                Objects.equals(categories, recipe.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coreRecipe, categories);
    }

}
