package org.flauschhaus.broccoli;

import android.app.Application;

import org.flauschhaus.broccoli.category.CategoryFragmentTest;
import org.flauschhaus.broccoli.di.ActivityModule;
import org.flauschhaus.broccoli.di.ApplicationComponent;
import org.flauschhaus.broccoli.di.FragmentModule;
import org.flauschhaus.broccoli.di.ViewModelModule;
import org.flauschhaus.broccoli.recipe.CreateAndEditRecipeActivityTest;
import org.flauschhaus.broccoli.recipe.MockRecipeModule;
import org.flauschhaus.broccoli.recipe.RecipeDetailsActivityTest;
import org.flauschhaus.broccoli.recipe.RecipeFragmentTest;
import org.flauschhaus.broccoli.recipe.cooking.CookingModeActivityTest;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        ViewModelModule.class,
        ActivityModule.class,
        FragmentModule.class,
        AndroidSupportInjectionModule.class,
        MockRecipeModule.class
})
public interface MockApplicationComponent extends ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        MockApplicationComponent.Builder application(Application application);

        MockApplicationComponent build();
    }

    void inject(CreateAndEditRecipeActivityTest test);
    void inject(RecipeDetailsActivityTest test);
    void inject(RecipeFragmentTest test);
    void inject(CategoryFragmentTest test);
    void inject(CookingModeActivityTest test);

}
