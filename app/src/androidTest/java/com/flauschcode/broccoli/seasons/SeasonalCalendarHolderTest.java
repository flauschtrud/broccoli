package com.flauschcode.broccoli.seasons;

import androidx.preference.PreferenceManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flauschcode.broccoli.BroccoliApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(AndroidJUnit4.class)
public class SeasonalCalendarHolderTest {

    private SeasonalCalendarHolder holder;

    @Before
    public void setUp() {
        holder = new SeasonalCalendarHolder(getApplication());
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

   @Test
   public void get_calendar_if_no_region_is_set() {
       PreferenceManager.getDefaultSharedPreferences(getApplication())
               .edit()
               .remove("seasonal-calendar-region")
               .apply();

       assertThat(holder.get().isPresent(), is(false));
   }

    @Test
    public void get_calendar() {
        PreferenceManager.getDefaultSharedPreferences(getApplication())
                .edit()
                .putString("seasonal-calendar-region", "flauschland")
                .apply();

        Optional<SeasonalCalendar> seasonalCalendarOptional = holder.get();
        assertThat(seasonalCalendarOptional.isPresent(), is(true));

        List<Month> augustDecember = new ArrayList<>();
        augustDecember.add(Month.AUGUST);
        augustDecember.add(Month.SEPTEMBER);
        augustDecember.add(Month.OCTOBER);
        augustDecember.add(Month.NOVEMBER);
        augustDecember.add(Month.DECEMBER);

        List<Month> octoberDecember = new ArrayList<>();
        octoberDecember.add(Month.OCTOBER);
        octoberDecember.add(Month.NOVEMBER);
        octoberDecember.add(Month.DECEMBER);

        SeasonalFood flauschfrucht = new SeasonalFood("flauschfrucht", "flauschfrucht", octoberDecember);
        SeasonalFood apple = new SeasonalFood("apple", "apple, apples", augustDecember);

        SeasonalCalendar seasonalCalendar = seasonalCalendarOptional.get();

        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.JANUARY), is(empty()));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.FEBRUARY), is(empty()));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.MARCH), is(empty()));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.APRIL), is(empty()));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.MAY), is(empty()));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.JUNE), is(empty()));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.JULY), is(empty()));

        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.AUGUST), hasSize(1));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.AUGUST), hasItem(apple));

        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.SEPTEMBER), hasSize(1));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.SEPTEMBER), hasItem(apple));

        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.OCTOBER), hasSize(2));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.OCTOBER), hasItem(apple));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.OCTOBER), hasItem(flauschfrucht));

        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.NOVEMBER), hasSize(2));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.NOVEMBER), hasItem(apple));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.NOVEMBER), hasItem(flauschfrucht));

        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.DECEMBER), hasSize(2));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.DECEMBER), hasItem(apple));
        assertThat(seasonalCalendar.getSeasonalFoodFor(Month.DECEMBER), hasItem(flauschfrucht));
    }

}