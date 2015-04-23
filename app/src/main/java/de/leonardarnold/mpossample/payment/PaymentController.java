package de.leonardarnold.mpossample.payment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Random;

import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.device.DeviceController;
import de.leonardarnold.mpossample.image.ImageCache;
import de.leonardarnold.mpossample.payment.exception.InvalidPaymentParameterException;
import de.leonardarnold.mpossample.payment.exception.SignatureException;
import de.leonardarnold.mpossample.payment.signature.SignatureActivity;
import de.leonardarnold.mpossample.session.data.SessionData;
import de.payleven.payment.Device;
import de.payleven.payment.DevicePreparationListener;
import de.payleven.payment.GeoLocation;
import de.payleven.payment.PairedDevice;
import de.payleven.payment.PaylevenError;
import de.payleven.payment.PaymentRequest;
import de.payleven.payment.PaymentResult;
import de.payleven.payment.PaymentTask;
import de.payleven.payment.SignatureResponseHandler;

/**
 * PaymentController does all the payment logic
 */
public class PaymentController {
    private static final String TAG = PaymentController.class.getSimpleName();
    // just a hardcoded location here ..
    private static final GeoLocation CURRENT_LOCATION = new GeoLocation(52.5075419, 13.4261419);

    private DeviceController mDeviceController;
    private Activity activity;
    private SessionData mSessionData;
    private boolean paymentCanceled;
    private PaymentTask paymentTask;
    private SignatureResponseHandler mSignatureResponseHandler;
    private ImageCache imageCache;
    private String generatedId;

    /**
     *  PaymentController should only be created with valid sessionData!
     *  DeviceController should also contain valid sessionData
     */
    public PaymentController(Activity activity, SessionData sessionData,
                             DeviceController deviceController, ImageCache imageCache) {
        this.activity = activity;
        mSessionData = sessionData;
        mDeviceController = deviceController;
        this.imageCache = imageCache;
    }

    /**
     *                          validates if the parameters used for payment are valid
     * @param amountString      charging amount
     * @param currency          used currency
     * @throws InvalidPaymentParameterException
     */
    public void validatePaymentParameters(String amountString, String currency)
            throws InvalidPaymentParameterException {
        if (amountString == null || amountString.isEmpty()) {
            throw new InvalidPaymentParameterException("Please enter an amount");
        }
        try {
            Log.d(TAG, parseInputAmount(amountString).toString());
        } catch (NumberFormatException e) {
            throw new InvalidPaymentParameterException("Amount not valid");
        }
        try {
            Currency.getInstance(currency);
        } catch (Exception e) {
            throw new InvalidPaymentParameterException("Currency not valid");
        }
        if (mDeviceController.getDefaultDevice() == null) {
            throw new InvalidPaymentParameterException("no default device selected");
        }
    }

    /**
     * parsing amound string to big integer
     */
    private BigDecimal parseInputAmount(String amountString) throws NumberFormatException {
        // take care here, in germany is not allowed to pay less then 1€
        // that means this logic is correct
        // if in your country more than 1€ is allowed you need to change this

        // if amount is lower than 100
        if(amountString.length() < 3)
            throw new NumberFormatException();

        amountString = amountString.substring(0, amountString.length()-2) + "." +
                amountString.substring(amountString.length()-2, amountString.length());

        return new BigDecimal(amountString);
    }

    /**
     * Prepare device for payment and then do the payment
     */
    public void prepareDeviceAndDoPayment(final String amountString, final String currencyString,
                                          final PaymentListener pListener)
            throws InvalidPaymentParameterException {
        // bool to check if payment canceled
        paymentCanceled = false;

        //validate parameters - throws exception if invalid
        validatePaymentParameters(amountString, currencyString);

        PairedDevice pairedDevice = mDeviceController.getDefaultDevice();

        final BigDecimal mAmount = parseInputAmount(amountString);
        final Currency currency = Currency.getInstance(currencyString);

        mSessionData.getPayleven().prepareDevice(pairedDevice, new DevicePreparationListener() {
                    @Override
                    public void onDone(Device preparedDevice) {
                        Log.d(TAG, "Device prepared");
                        startPayment(preparedDevice,
                                mAmount,
                                currency,
                                pListener);
                    }

                    @Override
                    public void onError(PaylevenError error) {
                        Log.e(TAG, error.getMessage(), error);
                        pListener.onError(error);
                    }
                }
        );
    }

    /**
     * starts the payment. will be called by prepareDeviceAndDoPayment
     * since the documentation says its good to prepare the device before
     * every payment
     */
    public void startPayment(Device device, BigDecimal amount, Currency currency,
                             final PaymentListener pListener) {
        //Generated unique payment id
        generatedId = Integer.toString(new Random(System.currentTimeMillis()).nextInt(99999999));
        //Current location of the device
        GeoLocation location = CURRENT_LOCATION;
        //generate payment request
        PaymentRequest paymentRequest = new PaymentRequest(amount, currency,
                generatedId, location);


        paymentTask = mSessionData.getPayleven().createPaymentTask(paymentRequest, device);
        paymentTask.startAsync(new de.payleven.payment.PaymentListener() {
            @Override
            public void onPaymentComplete(PaymentResult paymentResult) {
                Log.d(TAG, "Payment complete");
                // returns the payment result
                pListener.onPaymentComplete(paymentResult);
            }

            @Override
            public void onSignatureRequested(SignatureResponseHandler signatureHandler) {
                Log.d(TAG, "Signature requested");
                // 'save' SignatureResponseHandler
                mSignatureResponseHandler = signatureHandler;
                // start SignatureActivity
                Intent intent = new Intent(activity, SignatureActivity.class);
                intent.putExtra(Constants.UNIQUE_ID, generatedId);
                activity.startActivityForResult(intent, Constants.SIGNATURE_REQUEST_CODE);
            }

            @Override
            public void onError(PaylevenError error) {
                Log.d(TAG, "Payment error");
                pListener.onError(error);
            }
        });
        if (paymentCanceled)
            cancelPayment();
    }


    /**
     *
     * @param resultCode            if the activity was closed "OK"
     * @param data                  to know if the merchant accepted or
     *                              declined the signature
     * @throws SignatureException
     */
    public void signatureResult(int resultCode, Intent data) throws SignatureException {

        if (mSignatureResponseHandler == null || paymentTask == null) {
            throw new SignatureException("unknownState");
        }

        //If action was set in the Signature activity the merchant needs to
        // verify(confirm/decline) the signature
        if (resultCode == Activity.RESULT_OK) {
            //get Bitmap from ImageCache
            Bitmap signature = imageCache.getBitmapByIdAndClear(generatedId);
            //if signature is confirmed by merchant
            if (SignatureActivity.ACTION_CONFIRM.equals(data.getAction())) {
                mSignatureResponseHandler.confirmSignature(signature);
            } else {
                mSignatureResponseHandler.declineSignature(signature);
            }
        } else {
            //If merchant pressed Back while on the SignatureActivity, the payment must be cancelled
            cancelPayment();

        }

        //Cleanup to avoid keeping unused SignatureResponseHandler in memory
        mSignatureResponseHandler = null;

        //Doesn't make sense to cancel the payment after the signature was processed. So
        // clean up.
        paymentTask = null;


    }

    /**
     * @return a result Bitmap generated from @param paymentResult
     */
    public Bitmap prepareBitmapResult(PaymentResult paymentResult) {
        return paymentResult.getReceiptGenerator().generateReceipt(Constants.RECEIPT_WIDTH,
                Constants.RECEIPT_TEXT_SIZE);
    }

    /**
     * cancels the processing payment
     */
    public void cancelPayment() {
        paymentCanceled = true;
        try {
            paymentTask.cancel();
        } catch (Exception e) {
        }
        Log.d(TAG, "payment canceled");

    }

}
