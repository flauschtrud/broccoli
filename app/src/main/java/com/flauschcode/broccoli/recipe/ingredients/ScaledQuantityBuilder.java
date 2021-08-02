package com.flauschcode.broccoli.recipe.ingredients;

import android.app.Application;

import com.flauschcode.broccoli.R;

import java.text.DecimalFormat;
import java.text.Normalizer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScaledQuantityBuilder {

    private final Application application;

    @Inject
    public ScaledQuantityBuilder(Application application) {
        this.application = application;
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public String from(String quantity, float scaleFactor) {
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

    private String scale(String quantity, float scaleFactor) {
        if (isFraction(quantity)) {
            return scaleFraction(quantity, scaleFactor);
        }

        if (isVulgarFraction(quantity)) {
            return scaleVulgarFraction(quantity, scaleFactor);
        }

        float q = Float.parseFloat(quantity);
        return prettyPrint(q * scaleFactor);
    }

    private boolean isRange(String quantity) {
        return quantity.contains("-");
    }

    private boolean isFraction(String quantity) {
        return quantity.contains("/");
    }

    private String scaleFraction(String quantity, float scaleFactor) {
        String[] rat = quantity.split("/");
        float f = Float.parseFloat(rat[0]) / Float.parseFloat(rat[1]);
        return prettyPrint(f * scaleFactor);
    }

    // https://stackoverflow.com/a/26039424/5369519
    private boolean isVulgarFraction(String quantity) {
        return quantity.matches("[¼½¾⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞]");
    }

    private String scaleVulgarFraction(String quantity, float scaleFactor) {
        String[] fraction = Normalizer.normalize(quantity, Normalizer.Form.NFKD).split("\u2044");
        if (fraction.length == 2) {
            float f = (float) Integer.parseInt(fraction[0]) / Integer.parseInt(fraction[1]);
            return prettyPrint(f * scaleFactor);
        }
        throw new NumberFormatException();
    }

    private String prettyPrint(float f) {
        int i = (int) f;
        return f == i ? String.valueOf(i) : decimalFormat.format(f);
    }

    private String getNotScaledString() {
        return application.getString(R.string.unscaled);
    }

}
