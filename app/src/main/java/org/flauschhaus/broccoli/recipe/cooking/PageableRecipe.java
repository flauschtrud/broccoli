package org.flauschhaus.broccoli.recipe.cooking;

import java.util.ArrayList;
import java.util.List;

public class PageableRecipe {

    private List<Page> pages = new ArrayList<>();

    public List<Page> getPages() {
        return pages;
    }

    public void addPage(Page page) {
        this.pages.add(page);
    }

    static class Page {

        private String title;
        private String text;

        public Page(String title, String text) {
            this.title = title;
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }
    }
}
