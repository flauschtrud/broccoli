package com.flauschcode.broccoli.recipe.ingredients;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.R;

import java.text.DecimalFormat;
import java.text.Normalizer;

public class ScaledQuantity {

    private ScaledQuantity() {}

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public static String from(String quantity, float scaleFactor) {
        try {
            if (isRange(quantity)) {
                String[] ranges = quantity.replace(" ", "").split("-");
                String first = scale(ranges[0], scaleFactor);
                String second = scale(ranges[1], scaleFactor);
                return first + "-" + second;
            }

            return scale(quantity, scaleFactor);
        } catch (NumberFormatException e) {
            return "(" + getNotScaledString() + ") " + quantity;
        }
    }

    private static String scale(String quantity, float scaleFactor) {
        if (isFraction(quantity)) {
            return scaleFraction(quantity, scaleFactor);
        }

        if (isVulgarFraction(quantity)) {
            return scaleVulgarFraction(quantity, scaleFactor);
        }

        float q = Float.parseFloat(quantity);
        return prettyPrint(q * scaleFactor);
    }

    private static boolean isRange(String quantity) {
        return quantity.contains("-");
    }

    private static boolean isFraction(String quantity) {
        return quantity.contains("/");
    }

    private static String scaleFraction(String quantity, float scaleFactor) {
        String[] rat = quantity.split("/");
        float f = Float.parseFloat(rat[0]) / Float.parseFloat(rat[1]);
        return prettyPrint(f * scaleFactor);
    }

    // https://stackoverflow.com/a/26039424/5369519
    private static boolean isVulgarFraction(String quantity) {
        return quantity.matches("[¼½¾⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞]");
    }

    private static String scaleVulgarFraction(String quantity, float scaleFactor) {
        String[] fraction = Normalizer.normalize(quantity, Normalizer.Form.NFKD).split("\u2044");
        if (fraction.length == 2) {
            float f = (float) Integer.parseInt(fraction[0]) / Integer.parseInt(fraction[1]);
            return prettyPrint(f * scaleFactor);
        }
        throw new NumberFormatException();
    }

    private static String prettyPrint(float f) {
        int i = (int) f;
        return f == i ? String.valueOf(i) : decimalFormat.format(f);
    }

    private static String getNotScaledString() {
        return BroccoliApplication.getContext() != null? BroccoliApplication.getContext().getString(R.string.not_scaled) : "not scaled";
    }

}
