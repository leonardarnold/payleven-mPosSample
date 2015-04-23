package de.leonardarnold.mpossample.session;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.leonardarnold.mpossample.injection.ForApplication;
import de.leonardarnold.mpossample.injection.module.ApplicationModule;
import de.leonardarnold.mpossample.injection.module.ControllerModule;

@Module(
        includes = {ApplicationModule.class, ControllerModule.class},
        injects = {LoginActivity.class},
        library = true
)
public class LoginModule {

    @Provides
    @Singleton
    LoginController provideLoginController(@ForApplication Context context,
                                           SessionProvider sessionProvider) {
        return new LoginController(context, sessionProvider);
    }

}
