package com.flauschcode.broccoli.recipe.ingredients;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScaledQuantityTest {

    @Test
    public void scale_integer_quantity() {
        String scaled = ScaledQuantity.from("100", 2f);
        assertThat(scaled,  is("200"));
    }

    @Test
    public void scale_fraction_quantity() {
        String scaled = ScaledQuantity.from("1/2", 2f);
        assertThat(scaled,  is("1"));
    }

    @Test
    public void scale_vulgar_fraction_quantity() {
        String scaled = ScaledQuantity.from("½", 2f);
        assertThat(scaled,  is("1"));
    }

    @Test
    public void scale_range_quantity() {
        String scaled = ScaledQuantity.from("1 - 2", 2f);
        assertThat(scaled,  is("2-4"));
    }

    @Test
    public void scale_non_scalable_quantity() {
        String scaled = ScaledQuantity.from("je 1", 2f);
        assertThat(scaled,  is("(not scaled) je 1"));
    }

    @Test
    public void scale_missing_quantity() {
        String scaled = ScaledQuantity.from("", 2f);
        assertThat(scaled,  is("(not scaled) "));
    }

}