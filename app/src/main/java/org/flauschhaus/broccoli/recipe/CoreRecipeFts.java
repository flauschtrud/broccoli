package org.flauschhaus.broccoli.recipe;

import androidx.room.Entity;
import androidx.room.Fts4;

@Fts4(contentEntity = CoreRecipe.class, tokenizer = "unicode61", tokenizerArgs = "tokenchars=#")
@Entity(tableName = "recipes_fts")
public class CoreRecipeFts {

    private String title = "";
    private String description = "";
    private String source = "";
    private String ingredients = "";

    public CoreRecipeFts(String title, String description, String source, String ingredients) {
        this.title = title;
        this.description = description;
        this.source = source;
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public String getIngredients() {
        return ingredients;
    }

}
