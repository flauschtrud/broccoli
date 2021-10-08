package com.flauschcode.broccoli.recipe.cooking;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Servings {

    private final int quantity;
    private final String label;

    private static final Pattern patternQuantity = Pattern.compile("\\d+([,.])?\\d*(\\s*-\\s*(\\d+([,.])?\\d*))?");

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

    public static Optional<Servings> createFrom(String servingsInput) {
        if (servingsInput == null) {
            throw new IllegalArgumentException("Can not create Servings for null input.");
        }

        Matcher matcher = patternQuantity.matcher(servingsInput);
        String servings = matcher.find()? matcher.group() : "1";

        try {
            int quantity = Integer.parseInt(servings);
            String label = servingsInput.substring(servingsInput.indexOf(servings) + servings.length());
            return Optional.of(new Servings(quantity, label.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Servings unspecified() {
        return new Servings(1, null);
    }
}
