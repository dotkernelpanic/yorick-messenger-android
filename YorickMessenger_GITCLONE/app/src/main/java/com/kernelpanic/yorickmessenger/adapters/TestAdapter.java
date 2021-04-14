package com.kernelpanic.yorickmessenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.util.Device;
import com.pascalwelsch.arrayadapter.ArrayAdapter;

import java.util.List;

public class TestAdapter extends ArrayAdapter<Device, TestAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Device> devices;

    @Nullable
    @Override
    public Object getItemId(@NonNull Device item) {
        return null;
    }

    public TestAdapter(LayoutInflater inflater, List<Device> devices) {
        this.inflater = inflater;
        this.devices = devices;
    }

    @NonNull
    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_single_item, parent, false);
        return new TestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAdapter.ViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.singleItemText.setText(device.getDeviceName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView singleItemText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singleItemText = itemView.findViewById(R.id.recyclerViewSingleItemLabel);
        }
    }
}
