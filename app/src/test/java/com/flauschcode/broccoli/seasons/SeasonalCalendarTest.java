

package com.flauschcode.broccoli.seasons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

@RunWith(JUnit4.class)
public class SeasonalCalendarTest {

    @Test
    public void construct_and_use_calendar() {
        SeasonalCalendar seasonalCalendar = new SeasonalCalendar();

        SeasonalFood apple = new SeasonalFood("Apfel", "Apfel, Äpfel");
        SeasonalFood leek = new SeasonalFood("Lauch", "Lauch, Porree");

        seasonalCalendar.add(apple, Month.OCTOBER);
        seasonalCalendar.add(leek, Month.OCTOBER);

        List<SeasonalFood> seasonalFoodForOctober = seasonalCalendar.getSeasonalFoodFor(Month.OCTOBER);
        assertThat(seasonalFoodForOctober, hasSize(2));
        assertThat(seasonalFoodForOctober, hasItem(apple));
        assertThat(seasonalFoodForOctober, hasItem(leek));

        Set<String> searchTermsForOctober = seasonalCalendar.getSearchTermsFor(Month.OCTOBER);
        assertThat(searchTermsForOctober, hasSize(4));
        assertThat(searchTermsForOctober, hasItem("Apfel"));
        assertThat(searchTermsForOctober, hasItem("Äpfel"));
        assertThat(searchTermsForOctober, hasItem("Lauch"));
        assertThat(searchTermsForOctober, hasItem("Porree"));
    }

}