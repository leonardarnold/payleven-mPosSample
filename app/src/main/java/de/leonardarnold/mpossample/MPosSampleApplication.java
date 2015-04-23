package de.leonardarnold.mpossample;

import android.app.Application;

import java.util.Arrays;

import dagger.ObjectGraph;
import de.leonardarnold.mpossample.injection.module.ApplicationModule;
import de.leonardarnold.mpossample.injection.module.ControllerModule;
import de.leonardarnold.mpossample.session.LoginModule;

/**
 * using to create the ObjectGraph for using
 * dependency injection
 */
public class MPosSampleApplication extends Application {
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        //create graph with all modules
        mObjectGraph = ObjectGraph.create(getModules());
    }

    protected Object[] getModules() {
        //list all used modules
        return Arrays.asList(new ApplicationModule(this), new ControllerModule(), new LoginModule()).toArray();
    }

    //inject manually to graph
    public void inject(Object object) {
        mObjectGraph.inject(object);
    }
}
