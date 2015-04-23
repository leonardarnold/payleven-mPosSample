package de.leonardarnold.mpossample.injection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.MPosSampleApplication;

/**
 * Every activity should inherit from BaseActivity
 * it's needed for injection and for closing them in LoginActivity
 */
public abstract class BaseActivity extends Activity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Perform injection so that when this call returns all dependencies will be available for use.
        ((MPosSampleApplication) getApplication()).inject(this);

        final Activity activity = this;

        //register Broadcast receiver to close himself when a CLOSE_ALL broadcast was sent
        //(because LoginActivity is running in a different Task we send a broadcast to close
        //activities in different tasks
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.CLOSE_ALL);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, getClass().getSimpleName() + ": close_all broadcast received");
                activity.finish();
            }
        };
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregister receiver here
        unregisterReceiver(broadcastReceiver);
    }

}
