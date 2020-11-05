package com.flauschcode.broccoli;

import android.app.Application;

import com.flauschcode.broccoli.backup.BackupAndRestoreServiceTest;
import com.flauschcode.broccoli.category.CategoryFragmentTest;
import com.flauschcode.broccoli.di.ActivityModule;
import com.flauschcode.broccoli.di.ApplicationComponent;
import com.flauschcode.broccoli.di.FragmentModule;
import com.flauschcode.broccoli.di.ServiceModule;
import com.flauschcode.broccoli.di.ViewModelModule;
import com.flauschcode.broccoli.recipe.CreateAndEditRecipeActivityTest;
import com.flauschcode.broccoli.recipe.MockRecipeModule;
import com.flauschcode.broccoli.recipe.RecipeFragmentTest;
import com.flauschcode.broccoli.recipe.RecipeDetailsActivityTest;
import com.flauschcode.broccoli.recipe.cooking.CookingModeActivityTest;
import com.flauschcode.broccoli.recipe.importing.ImportableRecipeBuilderTest;
import com.flauschcode.broccoli.recipe.sharing.ShareRecipeAsFileServiceTest;

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
    void inject(ImportableRecipeBuilderTest test);
    void inject(ShareRecipeAsFileServiceTest test);
    void inject(BackupAndRestoreServiceTest test);

}
