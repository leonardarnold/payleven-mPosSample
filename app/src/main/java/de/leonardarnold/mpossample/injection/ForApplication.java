package de.leonardarnold.mpossample.injection;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Leonard Arnold on 09.04.15.
 */

@Qualifier
@Retention(RUNTIME)
public @interface ForApplication {
}
