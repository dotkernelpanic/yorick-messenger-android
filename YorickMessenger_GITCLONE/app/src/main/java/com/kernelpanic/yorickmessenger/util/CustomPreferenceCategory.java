package com.kernelpanic.yorickmessenger.util;

import android.content.Context;
import android.widget.TextView;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import com.kernelpanic.yorickmessenger.R;

public class CustomPreferenceCategory extends PreferenceCategory {

    Context context;

    public CustomPreferenceCategory(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView textView = (TextView) holder.findViewById(android.R.id.title);
        textView.setTextColor(context.getResources().getColor(R.color.prefsTextColor));
    }


}
