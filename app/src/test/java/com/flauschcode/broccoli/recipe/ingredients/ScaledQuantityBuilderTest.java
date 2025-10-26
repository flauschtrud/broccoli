package com.flauschcode.broccoli.recipe.ingredients;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScaledQuantityBuilderTest {

    private final ScaledQuantityBuilder scaledQuantityBuilder = new ScaledQuantityBuilder();

    @Test
    public void scale_integer_quantity() {
        String scaled = scaledQuantityBuilder.from("100", 2f);
        assertThat(scaled,  is("200"));
    }

    @Test
    public void scale_fraction_quantity() {
        String scaled = scaledQuantityBuilder.from("1/2", 2f);
        assertThat(scaled,  is("1"));
    }

    @Test
    public void scale_mixed_integer_quantity() {
        String scaled = scaledQuantityBuilder.from(" 1 1/2 ", 2f);
        assertThat(scaled,  is("3"));
    }

    @Test
    public void scale_vulgar_fraction_quantity() {
        String scaled = scaledQuantityBuilder.from(" ½", 2f);
        assertThat(scaled,  is("1"));
    }

    @Test
    public void scale_range_quantity() {
        String scaled = scaledQuantityBuilder.from("1 - 2", 2f);
        assertThat(scaled,  is("2-4"));
    }

    @Test
    public void scale_fraction_range_quantity() {
        String scaled = scaledQuantityBuilder.from("1/2 - 1", 2f);
        assertThat(scaled,  is("1-2"));
    }

    @Test
    public void scale_non_scalable_quantity() {
        String scaled = scaledQuantityBuilder.from("je 1", 2f);
        assertThat(scaled,  is("je 1"));
    }

    @Test
    public void scale_missing_quantity() {
        String scaled = scaledQuantityBuilder.from("", 2f);
        assertThat(scaled,  is(""));
    }

    @Test
    public void scale_incomplete_range() {
        String scaled = scaledQuantityBuilder.from("1-", 2f);
        assertThat(scaled,  is("1-"));
    }

    // see https://github.com/flauschtrud/broccoli/issues/318
    @Test
    public void scale_comma_quantity() {
        String scaled = scaledQuantityBuilder.from("0,5", 2f);
        assertThat(scaled,  is("1"));
    }

}