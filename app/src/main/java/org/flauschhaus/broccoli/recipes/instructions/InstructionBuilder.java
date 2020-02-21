package org.flauschhaus.broccoli.recipes.instructions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InstructionBuilder {

    private InstructionBuilder() {}

    public static List<Instruction> from(String instructions) {

        if (instructions ==  null) {
            return new ArrayList<>();
        }

        String[] split = Pattern.compile("\n").splitAsStream(instructions)
                .map(s -> s.replaceFirst("^\\s*(\\d+.)(?!$)", ""))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        return IntStream
                .range(0, split.length)
                .mapToObj(i -> new Instruction(String.valueOf(i+1), split[i]))
                .collect(Collectors.toList());
    }
}