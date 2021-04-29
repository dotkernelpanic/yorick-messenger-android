package com.kernelpanic.yorickmessenger.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.util.Device;

import java.util.ArrayList;
import java.util.List;


public class DevicesListAdapter extends ArrayAdapter<Device> {

    private List<Device> devices = new ArrayList<Device>();
    Context context;

    static class DeviceViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        RelativeLayout rootLayout;
    }

    public DevicesListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public void add(@Nullable Device object) {
        devices.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.devices.size();
    }

    @Nullable
    @Override
    public Device getItem(int position) {
        return this.devices.get(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DeviceViewHolder deviceViewHolder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_single_item, parent, false);
            deviceViewHolder = new DeviceViewHolder();
            deviceViewHolder.deviceName = (TextView) row.findViewById(R.id.deviceNameListViewLabel);
            deviceViewHolder.deviceAddress = (TextView) row.findViewById(R.id.deviceAddressListViewLabel);
            deviceViewHolder.rootLayout = (RelativeLayout) row.findViewById(R.id.singleItemRoot);
            row.setTag(deviceViewHolder);
        } else {
            deviceViewHolder = (DeviceViewHolder)row.getTag();
        }
        Device device = getItem(position);
        if (position % 2 == 0) {
            deviceViewHolder.rootLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.list_view_single_item_even));
        } else {
            deviceViewHolder.rootLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.list_view_single_item_odd));
        }
        if (device.getDeviceName() == null) {
            deviceViewHolder.deviceAddress.setText("");
        } else {
            deviceViewHolder.deviceName.setText(device.getDeviceName());
        }
        deviceViewHolder.deviceAddress.setText(device.getDeviceAdress());
        return row;
    }
}
