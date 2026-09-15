package com.flauschcode.broccoli.di;

import android.app.Application;

import androidx.databinding.DataBindingComponent;

import com.flauschcode.broccoli.recipe.images.ImageBindingAdapter;
import com.flauschcode.broccoli.seasons.SeasonsBindingAdapter;

import dagger.BindsInstance;
import dagger.Component;

@DataBinding
@Component(dependencies = ApplicationComponent.class, modules = BindingModule.class)
public interface BindingComponent extends DataBindingComponent {

    @Override
    ImageBindingAdapter getImageBindingAdapter();

    @Override
    SeasonsBindingAdapter getSeasonsBindingAdapter();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder applicationComponent(ApplicationComponent applicationComponent);

        BindingComponent build();
    }

    void inject(Application application);

}
