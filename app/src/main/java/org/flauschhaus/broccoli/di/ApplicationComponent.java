package org.flauschhaus.broccoli.di;

import android.app.Application;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.recipe.RecipeModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        ViewModelModule.class,
        ActivityModule.class,
        FragmentModule.class,
        ServiceModule.class,
        AndroidSupportInjectionModule.class,
        DatabaseModule.class,
        RecipeModule.class
})
public interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder database(DatabaseModule databaseModule);

        ApplicationComponent build();
    }

    void inject(BroccoliApplication application);
}
