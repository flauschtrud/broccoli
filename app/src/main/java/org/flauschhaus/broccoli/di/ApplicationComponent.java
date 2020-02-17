package org.flauschhaus.broccoli.di;

import android.app.Application;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.ui.recipes.NewRecipeViewModel;
import org.flauschhaus.broccoli.ui.recipes.RecipesViewModel;
import org.flauschhaus.broccoli.ui.settings.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DatabaseModule.class
})
public interface ApplicationComponent {

    void inject(BroccoliApplication application);
    void inject(NewRecipeViewModel newRecipeViewModel);
    void inject(RecipesViewModel recipesViewModel);
    void inject(SettingsFragment settingsFragment);

    Application getApplication();
}
