package de.leonardarnold.mpossample.injection.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.leonardarnold.mpossample.MPosSampleApplication;
import de.leonardarnold.mpossample.injection.ForApplication;

@Module(library = true)
public class ApplicationModule {
    private final MPosSampleApplication application;

    public ApplicationModule(MPosSampleApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    /* TODO for a valid payment wee need gps data
    @Provides @Singleton LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
    */

}
