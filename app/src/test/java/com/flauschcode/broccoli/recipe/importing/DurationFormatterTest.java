package com.flauschcode.broccoli.recipe.importing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class DurationFormatterTest {

    @Test
    public void format_durations() {
        Duration duration = Duration.ofDays(2).plusHours(4).plusMinutes(45);

        String formatted = DurationFormatter.format(duration);
        assertThat(formatted, is("2d 4h 45m"));
    }
}