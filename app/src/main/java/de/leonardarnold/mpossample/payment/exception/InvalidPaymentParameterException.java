package de.leonardarnold.mpossample.payment.exception;
public class InvalidPaymentParameterException extends Exception {
    public InvalidPaymentParameterException(String message){
        super(message);
    }
}
