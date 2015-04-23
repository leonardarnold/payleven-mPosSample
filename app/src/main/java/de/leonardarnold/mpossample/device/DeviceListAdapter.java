package de.leonardarnold.mpossample.device;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.payleven.payment.PairedDevice;

/**
 * DeviceListAdapter adapts the information to the ListView
 */
class DeviceListAdapter extends ArrayAdapter<PairedDevice> {
    // defaultDevice used for showing it bold
    private PairedDevice defaultDevice;

    DeviceListAdapter(Context context, List<PairedDevice> pairedDeviceList,
                      PairedDevice defaultDevice) {
        super(context, android.R.layout.simple_list_item_1, pairedDeviceList);
        this.defaultDevice = defaultDevice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //butterknife ViewHolder
        ViewHolder holder;
        View view = super.getView(position, convertView, parent);

        holder = new ViewHolder(view);
        //Don't forget the view holder for the real case
        PairedDevice device = getItem(position);
        holder.title.setText(device.getName());
        if (defaultDevice != null && defaultDevice.getId().equals(device.getId())) {
            //set font of default device to bold
            holder.title.setTypeface(null, Typeface.BOLD);
        } else {
            holder.title.setTypeface(null, Typeface.NORMAL);
        }
        return view;
    }

    // Butterknife view holder, to inject TextView
    static class ViewHolder {
        @InjectView(android.R.id.text1)
        TextView title;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

