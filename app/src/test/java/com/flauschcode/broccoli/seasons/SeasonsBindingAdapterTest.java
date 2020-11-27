package com.flauschcode.broccoli.seasons;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SeasonsBindingAdapterTest {

    @Mock
    SeasonalCalendarHolder seasonalCalendarHolder;

    @Mock
    SeasonalCalendar seasonalCalendar;

    private SeasonsBindingAdapter seasonsBindingAdapter;

    @Before
    public void setUp() {
        seasonsBindingAdapter = new SeasonsBindingAdapter(seasonalCalendarHolder);

        when(seasonalCalendarHolder.get()).thenReturn(Optional.of(seasonalCalendar));

        Set<String> searchTerms = new HashSet<>();
        searchTerms.add("Apfel");
        searchTerms.add("Auberginen");
        when(seasonalCalendar.getSearchTermsForCurrentMonth()).thenReturn(searchTerms);
    }

    @Test
    public void match_at_end_of_input() {
        assertThat(seasonsBindingAdapter.isSeasonal("2 Auberginen"), is(true));
    }

    @Test
    public void match_followed_by_comma() {
        assertThat(seasonsBindingAdapter.isSeasonal("1 Apfel, 2 Birnen"), is(true));
    }

    @Test
    public void match_followed_by_linebreak() {
        assertThat(seasonsBindingAdapter.isSeasonal("1 Apfel\n2 Birnen"), is(true));
    }

    @Test
    public void no_match_for_prefix() {
        assertThat(seasonsBindingAdapter.isSeasonal("Apfelessig"), is(false));
    }

}