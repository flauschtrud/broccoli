package com.flauschcode.broccoli.recipe.ingredients;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IngredientBuilder {

    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\n");

    private static final String VULGAR_FRACTION_PATTERN = "[¼½¾⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞]";
    private static final String DECIMAL_PATTERN = "\\d+([./,]\\d+)?";
    private static final String MIXED_INTEGER_PATTERN = "\\d+\\s+\\d+/\\d+";

    private static final String QUANTITY_PATTERN = "(" + VULGAR_FRACTION_PATTERN + "|" + MIXED_INTEGER_PATTERN  + "|" + DECIMAL_PATTERN + ")";

    private static final Pattern RANGE_PATTERN = Pattern.compile(
            "^" + QUANTITY_PATTERN + "(\\s*-\\s*" + QUANTITY_PATTERN + ")?"
    );

    private IngredientBuilder() {}

    public static List<Ingredient> from(String ingredients) {
        if (ingredients ==  null) {
            return new ArrayList<>();
        }

        return NEW_LINE_PATTERN.splitAsStream(ingredients)
                .map(s -> s.replaceFirst("^\\s*[-–](?!$)", ""))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    Matcher matcher = RANGE_PATTERN.matcher(s);
                    String quantity = matcher.find()? matcher.group() : "";
                    return new Ingredient(quantity, s.replaceFirst(quantity, ""));
                })
                .collect(Collectors.toList());
    }

}
