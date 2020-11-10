package com.flauschcode.broccoli.seasons;

import java.util.List;

class SeasonalCalendarJson {

    private List<SeasonalFoodJson> food;

    public void setFood(List<SeasonalFoodJson> food) {
        this.food = food;
    }

    public List<SeasonalFoodJson> getFood() {
        return food;
    }
}
