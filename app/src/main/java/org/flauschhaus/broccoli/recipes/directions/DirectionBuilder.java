package org.flauschhaus.broccoli.recipes.directions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DirectionBuilder {

    private static final Pattern newLinePattern = Pattern.compile("\n");

    private DirectionBuilder() {}

    public static List<Direction> from(String directions) {

        if (directions ==  null) {
            return new ArrayList<>();
        }

        String[] split = newLinePattern.splitAsStream(directions)
                .map(s -> s.replaceFirst("^\\s*(\\d+\\.)(?!$)", ""))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        return IntStream
                .range(0, split.length)
                .mapToObj(i -> new Direction(i+1, split[i]))
                .collect(Collectors.toList());
    }
}