package org.flauschhaus.broccoli.util;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewAssertion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class RecyclerViewAssertions {

    public static ViewAssertion hasItemsCount(final int count) {

        return (view, e) -> {
            if (!(view instanceof RecyclerView)) {
                throw e;
            }
            RecyclerView rv = (RecyclerView) view;
            assertThat(rv.getAdapter().getItemCount(),is(equalTo(count)));
        };
    }

}
