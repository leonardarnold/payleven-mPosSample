package de.leonardarnold.mpossample.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.R;
import de.leonardarnold.mpossample.injection.BaseActivity;

/**
 * shows the receipt as a bitmap
 */
public class ResultDetailsActivity extends BaseActivity {
    private static final String TAG = ResultDetailsActivity.class.getSimpleName();

    @InjectView(R.id.receipt_imageview)
    ImageView receiptImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        // receiving bitmap from intent extras
        Bitmap receipt = intent.getParcelableExtra(Constants.RECEIPT_BITMAP);
        // set receipt bitmap in view
        receiptImageView.setImageBitmap(receipt);
    }

    // closes the activity
    public void finishResultButton(View view){
        this.finish();
    }

}
