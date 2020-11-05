package com.flauschcode.broccoli.recipe.ingredients;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IngredientBuilder {

    private static final Pattern newLinePattern = Pattern.compile("\n");
    private static final Pattern quantityPattern = Pattern.compile("^\\s*(\\d+([,/.-])*\\d*)(?!$)");

    private IngredientBuilder() {}

    public static List<Ingredient> from(String ingredients) {
        if (ingredients ==  null) {
            return new ArrayList<>();
        }

        return newLinePattern.splitAsStream(ingredients)
                .map(s -> s.replaceFirst("^\\s*[-–](?!$)", ""))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    Matcher matcher = quantityPattern.matcher(s);
                    String quantity = matcher.find()? matcher.group() : "";
                    return new Ingredient(quantity, s.replaceFirst(quantity, ""));
                })
                .collect(Collectors.toList());
    }
}
