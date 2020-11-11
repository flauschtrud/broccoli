package com.flauschcode.broccoli.seasons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SeasonalFood {

    private String name;
    private List<String> terms;

    public SeasonalFood(String name, String terms) {
        this.name = name;
        this.terms = new ArrayList<>(Arrays.asList(terms.split("\\s*,\\s*")));
    }

    public String getName() {
        return name;
    }

    public List<String> getTerms() {
        return terms;
    }

    // TODO only temporarily
    @Override
    public String toString() {
        return "SeasonalFood{" +
                "name='" + name + '\'' +
                ", terms=" + terms +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeasonalFood that = (SeasonalFood) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(terms, that.terms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, terms);
    }
}
