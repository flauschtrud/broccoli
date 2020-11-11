package com.flauschcode.broccoli.seasons;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SeasonalCalendar {

    private Map<Month, List<SeasonalFood>> calendar;

    public SeasonalCalendar() {
        this.calendar = new EnumMap<>(Month.class);
        for (int i=1; i<=12; i++) {
            this.calendar.put(Month.of(i), new ArrayList<>());
        }
    }

    public List<SeasonalFood> getSeasonalFoodFor(Month month) {
        return calendar.get(month);
    }

    public Set<String> getSearchTermsForCurrentMonth() {
        Month currentMonth = Month.of(new DateTime().getMonthOfYear());
        return getSearchTermsFor(currentMonth);
    }

    public Set<String> getSearchTermsFor(Month month) {
        Set<String> searchTerms = new HashSet<>();
        getSeasonalFoodFor(month).forEach(seasonalFood -> searchTerms.addAll(seasonalFood.getTerms()));
        return searchTerms;
    }

    public void add(SeasonalFood seasonalFood, Month month) {
        calendar.get(month).add(seasonalFood);
    }

    // TODO only temporarily
    @Override
    public String toString() {
        return "SeasonalCalendar{" +
                "calendar=" + calendar +
                '}';
    }

}
