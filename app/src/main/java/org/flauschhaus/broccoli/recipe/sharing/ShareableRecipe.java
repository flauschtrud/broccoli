package org.flauschhaus.broccoli.recipe.sharing;

import android.net.Uri;

public class ShareableRecipe {

    private String plainText;
    private Uri imageUri;

    public ShareableRecipe(String plainText, Uri imageUri) {
        this.plainText = plainText;
        this.imageUri = imageUri;
    }

    public String getPlainText() {
        return plainText;
    }

    public Uri getImageUri() {
        return imageUri;
    }

}
