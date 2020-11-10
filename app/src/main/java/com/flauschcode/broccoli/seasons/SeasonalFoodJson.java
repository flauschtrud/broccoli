package com.flauschcode.broccoli.seasons;

import java.util.List;

class SeasonalFoodJson {

    private String name;
    private List<Month> months;

    public void setName(String name) {
        this.name = name;
    }

    public void setMonths(List<Month> months) {
        this.months = months;
    }

    public String getName() {
        return name;
    }

    public List<Month> getMonths() {
        return months;
    }

}
