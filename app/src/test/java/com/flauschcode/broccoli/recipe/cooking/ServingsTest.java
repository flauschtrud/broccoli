package com.flauschcode.broccoli.recipe.cooking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServingsTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_null_input() {
        Servings.createFrom(null);
    }

    @Test
    public void test_empty_input() {
        Servings servings = Servings.createFrom("  ");
        assertThat(servings.getQuantity(), is(1));
        assertThat(servings.getLabel(), is(""));
    }

    @Test
    public void test_missing_quantity() {
        Servings servings = Servings.createFrom("Stück");
        assertThat(servings.getQuantity(), is(1));
        assertThat(servings.getLabel(), is("Stück"));
    }

    @Test
    public void test_empty_label() {
        Servings servings = Servings.createFrom("4");
        assertThat(servings.getQuantity(), is(4));
        assertThat(servings.getLabel(), is(""));
    }

    @Test
    public void test_simple_quantity_and_label() {
        Servings servings = Servings.createFrom("4 Teller");
        assertThat(servings.getQuantity(), is(4));
        assertThat(servings.getLabel(), is("Teller"));
    }

    @Test
    public void test_long_label() {
        Servings servings = Servings.createFrom("12 Cookies (oder Kekse)");
        assertThat(servings.getQuantity(), is(12));
        assertThat(servings.getLabel(), is("Cookies (oder Kekse)"));
    }

    @Test
    public void test_text_before_quantity() {
        Servings servings = Servings.createFrom("ca. 8 Brötchen");
        assertThat(servings.getQuantity(), is(8));
        assertThat(servings.getLabel(), is("Brötchen"));
    }

    @Test
    public void test_range_quantity() {
        Servings servings = Servings.createFrom("35-40 Stück");
        assertThat(servings.getQuantity(), is(35));
        assertThat(servings.getLabel(), is("Stück"));
    }

    @Test
    public void test_decimal_quantity() {
        Servings servings = Servings.createFrom("1.5 Liter");
        assertThat(servings.getQuantity(), is(1));
        assertThat(servings.getLabel(), is("Liter"));
    }

}