package de.leonardarnold.mpossample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.leonardarnold.mpossample.device.DeviceActivity;
import de.leonardarnold.mpossample.device.DeviceController;
import de.leonardarnold.mpossample.image.ImageCache;
import de.leonardarnold.mpossample.injection.BaseActivity;
import de.leonardarnold.mpossample.payment.BoatRentalActivity;
import de.leonardarnold.mpossample.payment.PaymentController;
import de.leonardarnold.mpossample.payment.PaymentListener;
import de.leonardarnold.mpossample.payment.ResultActivity;
import de.leonardarnold.mpossample.payment.exception.InvalidPaymentParameterException;
import de.leonardarnold.mpossample.payment.exception.SignatureException;
import de.leonardarnold.mpossample.session.SessionProvider;
import de.leonardarnold.mpossample.session.data.SessionData;
import de.leonardarnold.mpossample.session.listener.SessionListener;
import de.leonardarnold.mpossample.view.PaymentNumpadView;
import de.payleven.payment.AuthorizationError;
import de.payleven.payment.BluetoothError;
import de.payleven.payment.NetworkError;
import de.payleven.payment.PaylevenError;
import de.payleven.payment.PaymentResult;
import de.payleven.payment.PaymentState;

/**
 * The MainActivity starts on android.intent.action.MAIN
 * With the injected SessionProvider we get valid SessionData.
 * If there is no data it will starts the LoginActivity to get
 * it. This data is used to manually create the controllers,
 * so you can be sure that the controllers have valid data.
 * Only on operations with the payleven object you have to take
 * a closer look at their documentation which errors can occur
 * as e.g. the Authorizationerror.
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOGIN_ACTIVITY_REQUEST = 1;
    private static final int VIEW_MODE_PAYMENT_PROGRESS = 0;
    private static final int VIEW_MODE_PAYMENT_DONE = 1;

    @Inject
    SessionProvider sessionProvider;
    @Inject
    ImageCache imageCache;

    @InjectView(R.id.payment_cancel_button)
    Button paymentCancelButton;
    @InjectView(R.id.payment_pay_button)
    Button paymentPayButton;
    @InjectView(R.id.amount_edittext)
    EditText amountEditText;
    @InjectView(R.id.currency_edittext)
    EditText currencyEditText;
    @InjectView(R.id.loading_progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.currency_info)
    TextView currencyInfoTextView;
    @InjectView(R.id.payment_numpad_view)
    PaymentNumpadView paymentNumpadView;

    private MenuItem deviceMenuItem;
    private MenuItem logoutMenuItem;
    private PaymentController mPaymentController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create mPaymentController with valid SessionData and valid DeviceController
        sessionProvider.getSessionData(new SessionListener() {
            @Override
            public void onRegistered(SessionData sessionData) {
                mPaymentController = createPaymentController(sessionData);
            }

            @Override
            public void onError(Exception e) {
                /* do anything */
            }
        });
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        //override onTouchEvent so it wont open the keyboard
        amountEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        //Get Numpad to set current activity to dispatch key events
        PaymentNumpadView paymentNumpadView = (PaymentNumpadView) findViewById(R.id.payment_numpad_view);
        paymentNumpadView.setActionListenerActivity(this);

    }

    /**
     * @param requestCode id to know which activity has sent the result code
     * @param resultCode  if everything went fine it should contain RESULT_OK
     *                    to know if merchant pressed back button while he was
     *                    on the signature screen
     * @param data        data passed in a intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if result comes from signature activity
        if (requestCode == Constants.SIGNATURE_REQUEST_CODE) {
            try {
                //finish signature payment flow
                mPaymentController.signatureResult(resultCode, data);
            } catch (SignatureException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            //if it comes from boatrental activity
        } else if (requestCode == Constants.BOATRENTAL_REQUEST_CODE) {
            //if no back button was pressed
            if (resultCode == RESULT_OK) {
                int tmp = data.getIntExtra(Constants.BOATRENTAL_AMOUNT, 0);
                if (tmp != 0) {
                    //set amount to edittext
                    amountEditText.setText(((Integer) tmp).toString());
                    //set cursor to end of edittext
                    amountEditText.setSelection(amountEditText.getText().length());
                }
            }

        }
    }

    /**
     * onClick method for pay button
     */
    public void onPayClicked(View view) {
        final Activity activity = this;
        //show progressbar is loading etc.
        setViewMode(VIEW_MODE_PAYMENT_PROGRESS);
        try {
            // prepare device and do payment
            mPaymentController.prepareDeviceAndDoPayment(amountEditText.getText().toString(),
                    currencyEditText.getText().toString(), new PaymentListener() {
                        @Override
                        public void onPaymentComplete(PaymentResult paymentResult) {
                            //when payment is finished or canceled
                            setViewMode(VIEW_MODE_PAYMENT_DONE);

                            //prepare result bitmap and payment state
                            // show it in new activity
                            showPaymentResult(mPaymentController.
                                    prepareBitmapResult(paymentResult), paymentResult.getState());
                        }

                        @Override
                        public void onError(PaylevenError error) {
                            // if an error occurs
                            setViewMode(VIEW_MODE_PAYMENT_DONE);
                            Log.d(TAG, error.getMessage());
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();

                            // if there is a authorization error, login in again
                            if (error instanceof AuthorizationError) {

                                sessionProvider.invalidateSessionData(new SessionListener() {
                                    @Override
                                    public void onRegistered(SessionData sessionData) {
                                        mPaymentController = new PaymentController(activity, sessionData,
                                                new DeviceController(activity, sessionData), imageCache);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        /* do nothing */
                                    }
                                });
                            } else if (error instanceof NetworkError) {
                                /* nothing */
                            } else if (error instanceof BluetoothError) {
                                /* nothing */
                            } else {
                                /* nothing */
                            }

                        }
                    });
            //if payment parameters are invalid
        } catch (InvalidPaymentParameterException e) {
            setViewMode(VIEW_MODE_PAYMENT_DONE);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * starts result activity to show the receipt as a Bitmap
     * since Bitmap is parcelable we can put it to the intent
     * because the receipt is small it's not a problem, but
     * for the signature it's safer to use the ImageCache
     *
     * @param receipt receipt as a Bitmap, generated by payleven mPosSdk
     */
    public void showPaymentResult(Bitmap receipt, PaymentState paymentState) {
        Intent intent = new Intent(this, ResultActivity.class);

        Bundle bundle = new Bundle();
        //paymentState is serializable
        bundle.putSerializable(Constants.PAYMENT_STATE, paymentState);
        //bitmap is parceable
        bundle.putParcelable(Constants.RECEIPT_BITMAP, receipt);
        intent.putExtras(bundle);

        this.startActivity(intent);
    }

    /**
     * onClick method for cancel button
     */
    public void onCancelClicked(View view) {
        mPaymentController.cancelPayment();
    }

    protected void setViewMode(int viewMode) {
        switch (viewMode) {
            // payment input mode
            case VIEW_MODE_PAYMENT_DONE:
                progressBar.setIndeterminate(false);
                paymentPayButton.setEnabled(true);
                deviceMenuItem.setEnabled(true);
                logoutMenuItem.setEnabled(true);
                paymentNumpadView.setEnabled(true);
                amountEditText.setEnabled(true);

                paymentCancelButton.setVisibility(View.GONE);
                currencyInfoTextView.setVisibility(View.VISIBLE);
                break;

            // payment in progress mode
            case VIEW_MODE_PAYMENT_PROGRESS:
                paymentPayButton.setEnabled(false);
                progressBar.setIndeterminate(true);
                deviceMenuItem.setEnabled(false);
                logoutMenuItem.setEnabled(false);
                paymentNumpadView.setEnabled(false);
                amountEditText.setEnabled(false);

                paymentCancelButton.setVisibility(View.VISIBLE);
                currencyInfoTextView.setVisibility(View.GONE);
                break;
            default:

        }
    }

    /**
     * Creating payment controller for further use
     *
     * @param sessionData valid session data
     * @return returns instance with valid SessionData
     */
    private PaymentController createPaymentController(SessionData sessionData) {
        return new PaymentController(this, sessionData,
                new DeviceController(this, sessionData), imageCache);
    }

    /**
     * getting menu items here so we can disable/enable it later
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        deviceMenuItem = menu.getItem(0);
        logoutMenuItem = menu.getItem(1);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * OnClick for actionbar items
     *
     * @param item selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            // if action_devices is clicked ->  show DeviceActivity
            case R.id.action_devices:
                Intent intent = new Intent(this, DeviceActivity.class);
                startActivity(intent);
                return true;
            // if action_logout is clicked ->   tell session provider to
            //                                  to delete all SessionData
            //                                  and login again
            case R.id.action_logout:
                sessionProvider.invalidateSessionData(new SessionListener() {
                    @Override
                    public void onRegistered(SessionData sessionData) {
                        //we need to create a new PaymentController with new SessionData
                        mPaymentController = createPaymentController(sessionData);
                    }

                    @Override
                    public void onError(Exception e) {
                        /* do nothing */
                    }
                });
                return true;
            case R.id.action_boatrental:
                intent = new Intent(this, BoatRentalActivity.class);
                startActivityForResult(intent, Constants.BOATRENTAL_REQUEST_CODE);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
