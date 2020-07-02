package org.flauschhaus.broccoli.recipe.sharing;

import org.flauschhaus.broccoli.recipe.Recipe;

import javax.inject.Singleton;

@Singleton
public class RecipeSharingService { // TODO test

    public String toHtml(Recipe recipe) {
        return "<html><body><h1>" + recipe.getTitle() + "</h1>" + recipe.getDescription() + "</body><html>"; // TODO StringBuilder
    }
}
