package com.flauschcode.broccoli.recipe.cooking;

import java.util.ArrayList;
import java.util.List;

public class PageableRecipe {

    private final List<Page> pages = new ArrayList<>();

    public List<Page> getPages() {
        return pages;
    }

    public void addPage(Page page) {
        this.pages.add(page);
    }

    record Page(String title, String text) {

    }
}
