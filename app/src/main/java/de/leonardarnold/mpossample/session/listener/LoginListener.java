package de.leonardarnold.mpossample.session.listener;

import de.payleven.payment.PaylevenError;

/**
 * Created by Leonard Arnold on 16.04.15.
 */
public interface LoginListener {
    void onLoginSuccessful();

    void onLoginError(PaylevenError e);
}
