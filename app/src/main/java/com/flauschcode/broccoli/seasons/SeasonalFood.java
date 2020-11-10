package com.flauschcode.broccoli.seasons;

import java.util.Objects;

public class SeasonalFood {

    private String name;
    private String terms;

    public SeasonalFood(String name, String terms) {
        this.name = name;
        this.terms = terms;
    }

    public String getName() {
        return name;
    }

    public String getTerms() {
        return terms;
    }

    // TODO only temporarily
    @Override
    public String toString() {
        return "SeasonalFood{" +
                "name='" + name + '\'' +
                ", terms='" + terms + '\'' +
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
