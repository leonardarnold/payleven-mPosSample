package de.leonardarnold.mpossample.payment.signature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.R;
import de.leonardarnold.mpossample.image.ImageCache;
import de.leonardarnold.mpossample.injection.BaseActivity;

/**TODO implement a real signature screen
 * here we're just passing an predefined image
 */

public class SignatureActivity extends BaseActivity {
    public static final String TAG = SignatureActivity.class.getSimpleName();
    public static final String ACTION_CONFIRM = "action_confirm";
    public static final String ACTION_DECLINE = "action_decline";

    @Inject
    ImageCache imageCache;

    @InjectView(R.id.signature_view)
    ImageView imageView;


    /**
     *  using ImageCache to store the bitmap and uuid
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.inject(this);

        Bitmap signature;
        String uuid;

        //get uuid from intent
        Intent intent = getIntent();
        uuid = intent.getStringExtra(Constants.UNIQUE_ID);

        //get Bitmap from imageview
        signature = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        //set image to imagecache
        imageCache.setImageById(uuid, signature);

    }

    /**
     * if signature was declined
     */
    public void declineButton(View view){
        setResult(RESULT_OK, new Intent(ACTION_DECLINE));
        finish();
    }

    /**
     * if signature was accepted
     */
    public void confirmButton(View view){
        setResult(RESULT_OK, new Intent(ACTION_CONFIRM));
        finish();
    }

}
