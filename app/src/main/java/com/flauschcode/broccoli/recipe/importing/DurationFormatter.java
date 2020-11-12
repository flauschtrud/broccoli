package com.flauschcode.broccoli.recipe.importing;

import java.time.Duration;

// java.time and Android don't yet have a way to get nice word based durations :(
class DurationFormatter {

    private DurationFormatter() {}

    static String format(Duration duration) {
        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();
        long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

        StringBuilder stringBuilder = new StringBuilder();
        if (days > 0) {
            stringBuilder.append(days).append("d ");
        }
        if (hours > 0) {
            stringBuilder.append(hours).append("h ");
        }
        if (minutes > 0) {
            stringBuilder.append(minutes).append("m");
        }

        return stringBuilder.toString();
    }
}
