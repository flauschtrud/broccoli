package com.flauschcode.broccoli.seasons;

import java.io.Serializable;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record SeasonalFood(String name, List<String> terms,
                           List<Month> months) implements Serializable {

    public SeasonalFood(String name, String terms, List<Month> months) {
        this.name = name;
        this.terms = new ArrayList<>(Arrays.asList(terms.split("\\s*,\\s*")));
        this.months = months;
    }

}
