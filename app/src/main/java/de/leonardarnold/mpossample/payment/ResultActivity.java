package de.leonardarnold.mpossample.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.R;
import de.leonardarnold.mpossample.injection.BaseActivity;
import de.payleven.payment.PaymentState;

/**
 * ResultActivity shows the payment state with
 * a details and with a continue button
 */
public class ResultActivity extends BaseActivity {
    private static final String TAG = ResultActivity.class.getSimpleName();

    @InjectView(R.id.result_payment_state_textview)
    TextView paymentStateTextView;

    Bitmap receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.inject(this);
        Intent intent = getIntent();

        // receiving passed bundle
        Bundle bundle = intent.getExtras();
        // receiving payment state
        PaymentState paymentState = (PaymentState) bundle.getSerializable(Constants.PAYMENT_STATE);
        // receiving receipt bitmap
        receipt = bundle.getParcelable(Constants.RECEIPT_BITMAP);

        //configure UI
        setPaymentStateUi(paymentState);
    }

    /**
     *                      set layout for specific transaction state
     * @param paymentState  transaction state
     */
    private void setPaymentStateUi(PaymentState paymentState){
        switch (paymentState){
            case APPROVED:
                Log.d(TAG, "payment state approved");
                paymentStateTextView.setText(R.string.result_payment_approved);
                paymentStateTextView.setBackgroundColor(getResources().
                        getColor(android.R.color.holo_green_light));
                break;
            case CANCELED:
                Log.d(TAG, "payment state canceled");
                paymentStateTextView.setText(R.string.result_payment_canceled);
                paymentStateTextView.setBackgroundColor(getResources().
                        getColor(android.R.color.holo_red_light));
                break;
            case DECLINED:
                Log.d(TAG, "payment state declined");
                paymentStateTextView.setText(R.string.result_payment_declined);
                paymentStateTextView.setBackgroundColor(getResources().
                        getColor(android.R.color.holo_red_light));
                break;
            default:
        }
    }

    public void continueButton(View view) {
        this.finish();
    }

    /**
     * starts ResultDetailsActivity which shows the receipt as a Bitmap
     */
    public void showDetailsButton(View view) {
        Intent intent = new Intent(this, ResultDetailsActivity.class);
        intent.putExtra(Constants.RECEIPT_BITMAP, receipt);
        this.startActivityForResult(intent, Constants.RESULT_DETAILS_REQUEST_CODE);
    }

    /**
     * if ResultDetailsActivity was finished, then finish this activity also
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RESULT_DETAILS_REQUEST_CODE){
            this.finish();
        }
    }

}
