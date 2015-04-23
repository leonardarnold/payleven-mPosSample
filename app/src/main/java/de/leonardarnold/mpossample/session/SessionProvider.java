package de.leonardarnold.mpossample.session;

import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.leonardarnold.mpossample.session.data.SessionData;
import de.leonardarnold.mpossample.session.listener.SessionListener;
import de.payleven.payment.Payleven;
import de.payleven.payment.PaylevenError;

/**
 * SessionProvider provides the session data
 * if no SessionData is available it starts
 * the LoginActivity. It passes the Data via an Listener
 * back. If somewhere e.g. an authorization error
 * occurs you can force a relogin with invalidateData
 */
public class SessionProvider implements StartLoginActivity {

    // using CopyOnWriteArrayList to avoid synchronizing problems
    // List for all SessionListeners registered
    private List<SessionListener> sessionListenerList;

    private SessionData sessionData;
    private Context context;

    public SessionProvider(Context context) {
        this.context = context;
        sessionListenerList = new CopyOnWriteArrayList<>();
    }

    /**
     * Returns directly the SessionData to the passed listener
     * if there is no data, it will start the LoginActivity and
     * adds the listener to a list to notify them when logged in
     * correctly
     */
    public void getSessionData(SessionListener mListener) {
        // start login activity if session data is null and add listener to listener list
        if (sessionData == null) {
            //only start login activity when listenerlist is empty
            if (sessionListenerList.isEmpty()) {
                startLoginActivity();
            }
            addSessionListener(mListener);
        } else {
            // if sessionData != null then payleven data inside sessiondData != null
            mListener.onRegistered(sessionData);
        }
    }

    /**
     * sets sessionData to null and starts getSessionData
     * it forces a re-login
     */
    public void invalidateSessionData(SessionListener mListener) {
        sessionData = null;
        getSessionData(mListener);
    }

    /**
     * starts LoginActivity
     * next step would be finish all other activities in the background
     * right now it starts the loginActivity on top of the stack
     * while overriding the back button method avoids to go back
     */
    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        // we're calling from outside an activity context, so we have to set this flag
        // this activity will become the start on his history stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);


    }

    /**
     * @param mSessionListener adds SessionListener to list
     */
    public void addSessionListener(SessionListener mSessionListener) {
        sessionListenerList.add(mSessionListener);
    }

    /**
     * notifies all listeners that we logged in successfully and delete them from list
     */
    public void notifyLogin(Payleven payleven) {
        // create new sessionData with payleven instance
        sessionData = new SessionData(payleven);

        Iterator<SessionListener> it = sessionListenerList.iterator();
        while (it.hasNext()) {
            it.next().onRegistered(sessionData);
        }
        // remove all listeners, so they won't be called again
        sessionListenerList.clear();

    }

    /**
     * notifies all listeners that we had an error while logging in and delete them from list
     */
    public void notifyError(PaylevenError paylevenError) {
        Iterator<SessionListener> it = sessionListenerList.iterator();
        while (it.hasNext()) {
            it.next().onError(paylevenError);
        }
        sessionListenerList.clear();
    }

}
