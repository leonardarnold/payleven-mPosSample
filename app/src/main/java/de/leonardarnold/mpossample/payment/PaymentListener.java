package de.leonardarnold.mpossample.payment;

import de.payleven.payment.PaylevenError;
import de.payleven.payment.PaymentResult;

public interface PaymentListener {
    void onPaymentComplete(PaymentResult paymentResult);
    void onError(PaylevenError error);
}
