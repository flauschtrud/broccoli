package com.flauschcode.broccoli.recipe.cooking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;

@RunWith(JUnit4.class)
public class ServingsTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_null_input() {
        Servings.createFrom(null);
    }

    @Test
    public void test_empty_input() {
        Optional<Servings> servings = Servings.createFrom("  ");
        assertThat(servings.isPresent(), is(true));
        assertThat(servings.get().getQuantity(), is(1));
        assertThat(servings.get().getLabel(), is(""));
    }

    @Test
    public void test_missing_quantity() {
        Optional<Servings> servings = Servings.createFrom("Stück");
        assertThat(servings.isPresent(), is(true));
        assertThat(servings.get().getQuantity(), is(1));
        assertThat(servings.get().getLabel(), is("Stück"));
    }

    @Test
    public void test_unspecified() {
        Servings servings = Servings.unspecified();
        assertThat(servings.getQuantity(), is(1));
        assertThat(servings.getLabel(), is(nullValue()));
    }

    @Test
    public void test_empty_label() {
        Optional<Servings> servings = Servings.createFrom("4");
        assertThat(servings.isPresent(), is(true));
        assertThat(servings.get().getQuantity(), is(4));
        assertThat(servings.get().getLabel(), is(""));
    }

    @Test
    public void test_simple_quantity_and_label() {
        Optional<Servings> servings = Servings.createFrom("4 Teller");
        assertThat(servings.isPresent(), is(true));
        assertThat(servings.get().getQuantity(), is(4));
        assertThat(servings.get().getLabel(), is("Teller"));
    }

    @Test
    public void test_long_label() {
        Optional<Servings> servings = Servings.createFrom("12 Cookies (oder Kekse)");
        assertThat(servings.isPresent(), is(true));
        assertThat(servings.get().getQuantity(), is(12));
        assertThat(servings.get().getLabel(), is("Cookies (oder Kekse)"));
    }

    @Test
    public void test_text_before_quantity() {
        Optional<Servings> servings = Servings.createFrom("ca. 8 Brötchen");
        assertThat(servings.isPresent(), is(true));
        assertThat(servings.get().getQuantity(), is(8));
        assertThat(servings.get().getLabel(), is("Brötchen"));
    }

    @Test
    public void test_second_number_in_label() {
        Optional<Servings> servings = Servings.createFrom("1 Brot (30cm Kastenform)");
        assertThat(servings.isPresent(), is(true));
        assertThat(servings.get().getQuantity(), is(1));
        assertThat(servings.get().getLabel(), is("Brot (30cm Kastenform)"));
    }

    @Test
    public void test_range_quantity() {
        Optional<Servings> servings = Servings.createFrom("35-40 Stück");
        assertThat(servings.isPresent(), is(false));
    }

    @Test
    public void test_decimal_quantity() {
        Optional<Servings> servings = Servings.createFrom("1.5 Liter");
        assertThat(servings.isPresent(), is(false));
    }

}