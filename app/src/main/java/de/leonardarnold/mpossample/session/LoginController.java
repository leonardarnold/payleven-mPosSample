package de.leonardarnold.mpossample.session;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.session.listener.LoginListener;
import de.leonardarnold.mpossample.session.listener.SessionListener;
import de.payleven.payment.Payleven;
import de.payleven.payment.PaylevenError;
import de.payleven.payment.PaylevenFactory;
import de.payleven.payment.PaylevenRegistrationListener;

/**
 * Created by Leonard Arnold on 09.04.15.
 */
class LoginController {
    private static final String TAG = LoginController.class.getSimpleName();
    //!-- injected
    SessionProvider sessionProvider;
    Context context;
    //--!

    public LoginController(Context context, SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
        this.context = context;
    }

    public void loginCanceled(){
        sessionProvider.notifyError(null);
        Intent intent = new Intent(Constants.CLOSE_ALL);
        context.sendBroadcast(intent);
        Log.d(TAG, "close all broadcast was sent");
    }

    /**
     * register payleven session asynchronous
     *
     * @param userName        payleven username
     * @param password        payleven password
     * @param loginListener will be notified after login action
     */
    public void loginAsync(String userName, String password,
                           final LoginListener loginListener) {

        // @param context payleven uses it internally
        PaylevenFactory.registerAsync(context, userName, password, Constants.APIKEY,
                new PaylevenRegistrationListener() {
                    @Override
                    public void onRegistered(Payleven payleven) {

                        //notify all listeners in list
                        sessionProvider.notifyLogin(payleven);
                        Log.d(TAG, "Payleven registered, notify all listeners");

                        //notify LoginActivity
                        loginListener.onLoginSuccessful();
                    }

                    @Override
                    public void onError(PaylevenError paylevenError) {

                        loginListener.onLoginError(paylevenError);

                    }
                });
    }
}
