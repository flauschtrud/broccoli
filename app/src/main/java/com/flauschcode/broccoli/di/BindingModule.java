package com.flauschcode.broccoli.di;

import android.app.Application;

import com.flauschcode.broccoli.recipe.images.ImageBindingAdapter;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.seasons.SeasonalCalendarHolder;
import com.flauschcode.broccoli.seasons.SeasonsBindingAdapter;

import dagger.Module;
import dagger.Provides;
import id.zelory.compressor.Compressor;

@Module
class BindingModule {

    @Provides
    @DataBinding
    ImageBindingAdapter imageBindingAdapter(RecipeImageService recipeImageService) {
        return new ImageBindingAdapter(recipeImageService);
    }

    @Provides
    @DataBinding
    SeasonsBindingAdapter seasonsBindingAdapter(SeasonalCalendarHolder seasonalCalendarHolder) {
        return new SeasonsBindingAdapter(seasonalCalendarHolder);
    }

    @Provides
    @DataBinding
    SeasonalCalendarHolder seasonalCalendarHolder(Application application) {
        SeasonalCalendarHolder seasonalCalendarHolder = new SeasonalCalendarHolder(application);
        seasonalCalendarHolder.preload();
        return seasonalCalendarHolder;
    }

    @Provides
    @DataBinding
    // Might make sense to refactor RecipeImageService since the method which uses the compressor is not really needed in DataBinding scope
    Compressor compressor(Application application) {
        return new Compressor(application);
    }
}
