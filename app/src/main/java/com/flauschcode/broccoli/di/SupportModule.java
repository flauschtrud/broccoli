package com.flauschcode.broccoli.di;

import com.flauschcode.broccoli.support.RatingService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SupportModule {

    @Provides
    @Singleton
    RatingService ratingService() {
        return new RatingService();
    }
}
