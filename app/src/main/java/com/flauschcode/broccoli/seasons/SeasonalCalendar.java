package com.flauschcode.broccoli.seasons;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SeasonalCalendar {

    private final Map<Month, List<SeasonalFood>> calendar;

    public SeasonalCalendar() {
        this.calendar = new EnumMap<>(Month.class);
        Arrays.stream(Month.values()).forEach(month -> this.calendar.put(month, new ArrayList<>()));
    }

    public List<SeasonalFood> getSeasonalFoodFor(Month month) {
        return calendar.get(month);
    }

    public Set<String> getSearchTermsForCurrentMonth() {
        return getSearchTermsFor(LocalDate.now().getMonth());
    }

    public Set<String> getSearchTermsFor(Month month) {
        Set<String> searchTerms = new HashSet<>();
        getSeasonalFoodFor(month).forEach(seasonalFood -> searchTerms.addAll(seasonalFood.getTerms()));
        return searchTerms;
    }

    public void add(SeasonalFood seasonalFood, Month month) {
        calendar.get(month).add(seasonalFood);
    }

}
