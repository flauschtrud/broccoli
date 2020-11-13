package com.flauschcode.broccoli.seasons;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SeasonalFood {

    private final String name;
    private final List<String> terms;
    private final List<Month> months;

    public SeasonalFood(String name, String terms, List<Month> months) {
        this.name = name;
        this.terms = new ArrayList<>(Arrays.asList(terms.split("\\s*,\\s*")));
        this.months = months;
    }

    public String getName() {
        return name;
    }

    public List<String> getTerms() {
        return terms;
    }

    public List<Month> getMonths() {
        return months;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeasonalFood that = (SeasonalFood) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(terms, that.terms) &&
                Objects.equals(months, that.months);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, terms, months);
    }
}
