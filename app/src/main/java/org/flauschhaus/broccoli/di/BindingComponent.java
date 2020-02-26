package org.flauschhaus.broccoli.di;

import android.app.Application;

import dagger.BindsInstance;
import dagger.Component;

@DataBinding
@Component(dependencies = ApplicationComponent.class, modules = BindingModule.class)
public interface BindingComponent extends androidx.databinding.DataBindingComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder applicationComponent(ApplicationComponent applicationComponent);

        BindingComponent build();
    }

    void inject(Application application);

}
