package de.leonardarnold.mpossample.session.data;


import de.payleven.payment.Payleven;

/**
 * Here you can store your user specific session data
 */
public class SessionData {
    private Payleven payleven;

    public SessionData(Payleven payleven) {
        this.payleven = payleven;
    }
    public Payleven getPayleven() {
        return payleven;
    }
}
