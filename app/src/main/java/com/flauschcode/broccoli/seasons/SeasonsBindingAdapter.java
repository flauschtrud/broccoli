package com.flauschcode.broccoli.seasons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.databinding.IngredientItemBinding;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SeasonsBindingAdapter {

    private SeasonalCalendarHolder seasonalCalendarHolder;

    @Inject
    public SeasonsBindingAdapter(SeasonalCalendarHolder seasonalCalendarHolder) {
        this.seasonalCalendarHolder = seasonalCalendarHolder;
    }

    @BindingAdapter(value = {"ingredients"}, requireAll = false)
    public void bind(ImageView imageView, String ingredients) {
        imageView.setVisibility(isSeasonal(ingredients)? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("ingredients")
    public void bindIngredients(LinearLayout layout, String ingredients) {
        layout.removeAllViews();

        LayoutInflater inflater = getLayoutInflater(layout);

        IngredientBuilder.from(ingredients).forEach(ingredient -> {
            ingredient.setSeasonal(isSeasonal(ingredient.getText()));
            IngredientItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.ingredient_item, layout, true);
            binding.setIngredient(ingredient);
        });
    }

    private LayoutInflater getLayoutInflater(LinearLayout layout) {
        return (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private boolean isSeasonal(String ingredientText) {
        Optional<SeasonalCalendar> seasonalCalendarOptional = seasonalCalendarHolder.get();
        if (seasonalCalendarOptional.isPresent()) {
            SeasonalCalendar seasonalCalendar = seasonalCalendarOptional.get();
            Pattern pattern = Pattern.compile(seasonalCalendar.getSearchTermsForCurrentMonth().stream().collect(Collectors.joining("|")));
            Matcher matcher = pattern.matcher(ingredientText);
            return matcher.find();
        }
        return false;
    }

}
