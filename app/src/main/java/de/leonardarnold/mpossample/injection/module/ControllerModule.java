package de.leonardarnold.mpossample.injection.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.leonardarnold.mpossample.MainActivity;
import de.leonardarnold.mpossample.device.DeviceActivity;
import de.leonardarnold.mpossample.image.ImageCache;
import de.leonardarnold.mpossample.injection.ForApplication;
import de.leonardarnold.mpossample.payment.BoatRentalActivity;
import de.leonardarnold.mpossample.payment.ResultActivity;
import de.leonardarnold.mpossample.payment.ResultDetailsActivity;
import de.leonardarnold.mpossample.payment.signature.SignatureActivity;
import de.leonardarnold.mpossample.session.SessionProvider;

@Module(includes = {ApplicationModule.class},
        injects = {MainActivity.class, SignatureActivity.class,
                DeviceActivity.class, SessionProvider.class, ResultActivity.class, ResultDetailsActivity.class,
                BoatRentalActivity.class},
        library = true)
public class ControllerModule {

    @Provides
    @Singleton
    SessionProvider provideSessionProvider(@ForApplication Context context) {
        return new SessionProvider(context);
    }

    @Provides
    @Singleton
    ImageCache provideImageCacheProvider() {
        return new ImageCache();
    }

}
