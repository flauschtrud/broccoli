package com.flauschcode.broccoli.recipe.cooking;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.flauschcode.broccoli.recipe.Recipe;

import java.util.Objects;
import java.util.Optional;

public class ScalingDialogViewModel extends ViewModel {

    private Servings servings;

    private final ObservableField<Boolean> simpleMode = new ObservableField<>(true);
    private final ObservableField<String> numberOfServings = new ObservableField<>();
    private final ObservableField<String> scaleFactor = new ObservableField<>("1.0");

    public void setRecipe(Recipe recipe) {
        this.servings = Servings.createFrom(recipe.getServings());
        this.numberOfServings.set(String.valueOf(servings.getQuantity()));
    }

    public void enableSimpleMode() {
        simpleMode.set(true);
    }

    public void disableSimpleMode() {
        simpleMode.set(false);
    }

    public ObservableField<Boolean> isSimpleMode() {
        return simpleMode;
    }

    public Servings getServings() {
        return servings;
    }

    public ObservableField<String> getNumberOfServings() {
        return numberOfServings;
    }

    public ObservableField<String> getScaleFactor() {
        return scaleFactor;
    }

    public void incrementNumberOfServings() {
        safeGetNumberOfServings().ifPresent(i -> this.numberOfServings.set(String.valueOf(i + 1)));
    }

    public void decrementNumberOfServings() {
        safeGetNumberOfServings().ifPresent(i -> {
            if (i >= 1) {
                this.numberOfServings.set(String.valueOf(i - 1));
            }
        });
    }

    public Optional<Float> computeScaleFactor() {
        if (servings == null) {
            throw new IllegalArgumentException("Can not compute scale factor for missing Servings. A Recipe must be set.");
        }

        if (Boolean.TRUE.equals(simpleMode.get())) {
            return safeGetNumberOfServings().map(integer -> (float) integer / servings.getQuantity());
        } else {
            return safeGetScaleFactor();
        }
    }

    private Optional<Integer> safeGetNumberOfServings() {
        try {
            return Optional.of(Integer.parseInt(Objects.requireNonNull(numberOfServings.get())));

        } catch (NumberFormatException e) {
            Log.e(getClass().getName(), e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Float> safeGetScaleFactor() {
        try {
            return Optional.of(Float.parseFloat(Objects.requireNonNull(scaleFactor.get())));

        } catch (NumberFormatException e) {
            Log.e(getClass().getName(), e.getMessage());
            return Optional.empty();
        }
    }
}
