package org.flauschhaus.broccoli;

import android.app.Application;

import org.flauschhaus.broccoli.di.ApplicationComponent;
import org.flauschhaus.broccoli.di.ApplicationModule;
import org.flauschhaus.broccoli.di.DaggerApplicationComponent;
import org.flauschhaus.broccoli.di.DatabaseModule;

public class BroccoliApplication extends Application {

    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

}
