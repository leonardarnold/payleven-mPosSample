package de.leonardarnold.mpossample.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;

import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.session.data.SessionData;
import de.payleven.payment.PairedDevice;

public class DeviceController {
    private static final String TAG = DeviceController.class.getSimpleName();

    private Context context;
    private SessionData mSessionData;

    public DeviceController(Context context, SessionData sessionData) {
        this.context = context;
        this.mSessionData = sessionData;
    }

    /**
     * @return paired device list
     */
    public List<PairedDevice> getPairedDeviceList() {
        return mSessionData.getPayleven().getPairedDevices();
    }

    /**
     * @param pairedDevice to store as default device in preferences
     */
    public void setDefaultDevice(PairedDevice pairedDevice) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.DEFAULT_DEVICE_ID, pairedDevice.getId());
        editor.apply();
    }

    /**
     * compares stored default device with paired payleven devices and return it if available
     *
     * @return default pairedDevice
     */
    public PairedDevice getDefaultDevice() {
        // get stored device uuid from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultDeviceId = prefs.getString(Constants.DEFAULT_DEVICE_ID, null);

        // if there was no uuid
        if (defaultDeviceId == null) {
            return null;
        }

        if (mSessionData != null) {
            List<PairedDevice> deviceList = getPairedDeviceList();
            // get PairedDevice from the stored defaultDeviceId
            for (PairedDevice device : deviceList) {
                if (device.getId().equals(defaultDeviceId)) {
                    return device;
                }
            }
        }

        // if device is not found, delete in preferences and return null
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.DEFAULT_DEVICE_ID, null);
        editor.apply();
        return null;
    }


}
