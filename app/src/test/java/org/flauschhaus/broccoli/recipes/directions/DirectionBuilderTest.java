package org.flauschhaus.broccoli.recipes.directions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(JUnit4.class)
public class DirectionBuilderTest {

    private List<Direction> directions;

    @Test
    public void empty_string_should_yield_empty_list() {
        directions = DirectionBuilder.from("");
        assertThat(directions, is(empty()));
    }

    @Test
    public void null_should_yield_empty_list() {
        directions = DirectionBuilder.from(null);
        assertThat(directions, is(empty()));
    }

    @Test
    public void single_paragraph_should_yield_one_direction() {
        directions = DirectionBuilder.from("Erstmal den Teig machen.");
        assertThat(directions, hasSize(1));
        assertThat(directions, hasItem(new Direction(1, "Erstmal den Teig machen.")));
    }

    @Test
    public void directions_should_be_split_by_new_lines() {
        directions = DirectionBuilder.from("Erstmal den Teig machen.\nLauch schneiden und kochen.\nAb in den Backofen!");
        assertThat(directions, hasSize(3));
        assertThat(directions, hasItem(new Direction(1, "Erstmal den Teig machen.")));
        assertThat(directions, hasItem(new Direction(2, "Lauch schneiden und kochen.")));
        assertThat(directions, hasItem(new Direction(3, "Ab in den Backofen!")));
    }

    @Test
    public void empty_lines_and_tabs_should_be_skipped() {
        directions = DirectionBuilder.from("   Erstmal den Teig machen.\n \n Lauch schneiden und kochen.\n \tAb in den Backofen!");
        assertThat(directions, hasSize(3));
        assertThat(directions, hasItem(new Direction(1, "Erstmal den Teig machen.")));
        assertThat(directions, hasItem(new Direction(2, "Lauch schneiden und kochen.")));
        assertThat(directions, hasItem(new Direction(3, "Ab in den Backofen!")));
    }

    @Test
    public void preceding_numerations_should_be_omitted() {
        directions = DirectionBuilder.from("1. Erstmal den Teig machen.\n 2. Lauch schneiden und kochen.\n 3. Ab in den 2. Backofen!");
        assertThat(directions, hasSize(3));
        assertThat(directions, hasItem(new Direction(1, "Erstmal den Teig machen.")));
        assertThat(directions, hasItem(new Direction(2, "Lauch schneiden und kochen.")));
        assertThat(directions, hasItem(new Direction(3, "Ab in den 2. Backofen!")));
    }

}