package com.flauschcode.broccoli.recipe.cooking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Servings {

    private final int quantity;
    private final String label;

    private static final Pattern patternQuantity = Pattern.compile("\\d+");
    private static final Pattern patternLabel = Pattern.compile("\\D*$");

    public Servings(int quantity, String label) {
        this.quantity = quantity;
        this.label = label;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLabel() {
        return label;
    }

    public static Servings createFrom(String servingsInput) {
        if (servingsInput == null) {
            throw new IllegalArgumentException("Can not create Servings for null input.");
        }

        Matcher matcher = patternQuantity.matcher(servingsInput);
        String servings = matcher.find()? matcher.group() : "1";
        int quantity = Integer.parseInt(servings);

        Matcher matcherServings = patternLabel.matcher(servingsInput);
        String label = matcherServings.find() ? matcherServings.group() : "";

        return new Servings(quantity, label.trim());
    }
}
