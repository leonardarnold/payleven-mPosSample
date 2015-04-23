package de.leonardarnold.mpossample.device;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.leonardarnold.mpossample.R;
import de.leonardarnold.mpossample.injection.BaseActivity;
import de.leonardarnold.mpossample.session.SessionProvider;
import de.leonardarnold.mpossample.session.data.SessionData;
import de.leonardarnold.mpossample.session.listener.SessionListener;
import de.payleven.payment.PairedDevice;

public class DeviceActivity extends BaseActivity {
    private static final String TAG = DeviceActivity.class.getSimpleName();
    private static final int VIEW_MODE_NO_DEVICES = 1;
    private static final int VIEW_MODE_SHOW_DEVICES = 2;
    private static final int VIEW_MODE_SHOW_DEVICES_NO_DEFAULT_DEVICE = 3;

    @Inject
    SessionProvider mSessionProvider;

    @InjectView(R.id.device_listview)
    ListView deviceListView;
    @InjectView(R.id.device_info_text)
    TextView noDeviceInfoTextView;
    @InjectView(R.id.select_default_device_info)
    TextView selectDefaultDeviceTextView;

    MenuItem finishMenuItem;

    private DeviceController deviceController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.inject(this);
        final Context context = this;
        /*
            creating devicecontroller with valid payleven object.
            doesn't need to throw any exception (!)
         */
        mSessionProvider.getSessionData(new SessionListener() {
            @Override
            public void onRegistered(SessionData sessionData) {
                // create deviceController with valid payleven data
                deviceController = new DeviceController(context, sessionData);
                // call updateList with valid sessionData
                updateList(deviceController.getDefaultDevice());
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    // update device list
    @Override
    protected void onResume() {
        super.onResume();
        //does not update list if device controller is not created
        //updateList will be called directly after a valid sessiondata is present
        if (deviceController != null)
            updateList(deviceController.getDefaultDevice());
    }

    /**
     * @param viewMode set view mode, e.g. you log in so you should disable the login button
     */
    protected void setViewMode(int viewMode) {
        switch (viewMode) {
            //no devices found
            case VIEW_MODE_NO_DEVICES:
                deviceListView.setVisibility(View.GONE);
                noDeviceInfoTextView.setVisibility(View.VISIBLE);
                selectDefaultDeviceTextView.setVisibility(View.GONE);
                break;
            //devices found
            case VIEW_MODE_SHOW_DEVICES:
                deviceListView.setVisibility(View.VISIBLE);
                noDeviceInfoTextView.setVisibility(View.GONE);
                selectDefaultDeviceTextView.setVisibility(View.GONE);
                break;
            //devices found, but no default device was selected
            case VIEW_MODE_SHOW_DEVICES_NO_DEFAULT_DEVICE:
                deviceListView.setVisibility(View.VISIBLE);
                noDeviceInfoTextView.setVisibility(View.GONE);
                selectDefaultDeviceTextView.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    /**
     * Update the ListView
     */
    private void updateList(PairedDevice defaultDevice) {
        final Activity activity = this;
        List<PairedDevice> pairedDeviceList = deviceController.getPairedDeviceList();
        if (pairedDeviceList.size() > 0) {

            // show info when no default device is selected
            if (defaultDevice == null) {
                setViewMode(VIEW_MODE_SHOW_DEVICES_NO_DEFAULT_DEVICE);
            } else {
                setViewMode(VIEW_MODE_SHOW_DEVICES);
            }

            final ArrayAdapter<PairedDevice> adapter = new DeviceListAdapter(this, pairedDeviceList,
                    defaultDevice);
            deviceListView.setAdapter(adapter);
            deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PairedDevice pairedDevice = (PairedDevice) parent.getItemAtPosition(position);
                    deviceController.setDefaultDevice(pairedDevice);
                    Toast.makeText(activity, pairedDevice.getName() + " selected as default",
                            Toast.LENGTH_SHORT).show();
                    updateList(pairedDevice);
                }
            });
        } else {
            //show info how to pair with shuttle if not paired with any
            setViewMode(VIEW_MODE_NO_DEVICES);
        }
    }

    /**
     * getting menu items here so we can disable/enable it later
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_devices, menu);
        finishMenuItem = menu.getItem(0);
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
            case R.id.action_finish:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
