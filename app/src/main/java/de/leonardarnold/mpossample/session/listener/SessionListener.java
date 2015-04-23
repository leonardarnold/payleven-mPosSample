package de.leonardarnold.mpossample.session.listener;

import de.leonardarnold.mpossample.session.data.SessionData;

/**
 * The SessionListener notifies when new Sessiondata is available
 * or when an error occured
 */
public interface SessionListener {
    void onRegistered(SessionData sessionData);
    void onError(Exception e);
}
