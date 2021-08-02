package com.flauschcode.broccoli.recipe.ingredients;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import android.app.Application;

import com.flauschcode.broccoli.R;

@RunWith(MockitoJUnitRunner.class)
public class ScaledQuantityBuilderTest {

    @Mock
    private Application application;

    @InjectMocks
    private ScaledQuantityBuilder scaledQuantityBuilder;

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
    public void scale_vulgar_fraction_quantity() {
        String scaled = scaledQuantityBuilder.from("½", 2f);
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
        when(application.getString(R.string.unscaled)).thenReturn("not scaled");
        String scaled = scaledQuantityBuilder.from("je 1", 2f);
        assertThat(scaled,  is("(not scaled) je 1"));
    }

    @Test
    public void scale_missing_quantity() {
        when(application.getString(R.string.unscaled)).thenReturn("not scaled");
        String scaled = scaledQuantityBuilder.from("", 2f);
        assertThat(scaled,  is("(not scaled) "));
    }

}