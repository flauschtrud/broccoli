package com.flauschcode.broccoli.seasons;

import android.content.res.Resources;
import android.icu.text.Collator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.RecyclerViewAdapter;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class MonthFragment extends Fragment {

    @Inject
    SeasonalCalendarHolder seasonalCalendarHolder;

    private final Month month;

    public MonthFragment(Month month) {
        super();
        this.month = month;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_month, container, false);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        ListAdapter<SeasonalFood, RecyclerViewAdapter<SeasonalFood>.Holder> adapter = new RecyclerViewAdapter<SeasonalFood>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.seasonal_food_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.seasonalFood;
            }

            @Override
            protected void onItemClick(SeasonalFood seasonalFood) {
                SeasonsFragmentDirections.ActionSearchForSeasonalFood actionSearchForSeasonalFood = SeasonsFragmentDirections.actionSearchForSeasonalFood(seasonalFood);
                navController.navigate(actionSearchForSeasonalFood);
            }
        };
        recyclerView.setAdapter(adapter);

        Collator collator = Collator.getInstance(getResources().getConfiguration().getLocales().get(0));
        seasonalCalendarHolder.get().ifPresent(seasonalCalendar -> adapter.submitList(seasonalCalendar.getSeasonalFoodFor(month).stream().sorted(Comparator.comparing(SeasonalFood::getName, collator)).collect(Collectors.toList())));

        return root;
    }

    @BindingAdapter("months")
    public static void bind(TextView textView, List<Month> months) {
        Month first = months.get(0);
        Month last = months.get(months.size()-1);

        Resources resources = textView.getContext().getResources();
        Locale locale = resources.getConfiguration().getLocales().get(0);

        String firstDisplayName = first.getDisplayName(TextStyle.SHORT_STANDALONE, locale);
        String lastDisplayName = last.getDisplayName(TextStyle.SHORT_STANDALONE, locale);

        textView.setText(resources.getString(R.string.seasons_month_range, firstDisplayName, lastDisplayName));
    }

}