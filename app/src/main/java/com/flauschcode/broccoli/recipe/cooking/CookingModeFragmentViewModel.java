package com.flauschcode.broccoli.recipe.cooking;

import androidx.lifecycle.ViewModel;

public class CookingModeFragmentViewModel extends ViewModel {

    private int position = 0;
    private int maxSteps = 0;
    private String title = "";
    private String text = "";

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
