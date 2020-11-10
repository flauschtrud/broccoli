package com.flauschcode.broccoli.seasons;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SeasonalCalendar {

    private Map<Month, List<SeasonalFood>> calendar;

    public SeasonalCalendar() {
        this.calendar = new EnumMap<>(Month.class);
        for (int i=0; i<12; i++) {
            this.calendar.put(Month.values()[i], new ArrayList<>());
        }
    }

    public List<SeasonalFood> getSeasonalFoodFor(Month month) {
        return calendar.get(month);
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
