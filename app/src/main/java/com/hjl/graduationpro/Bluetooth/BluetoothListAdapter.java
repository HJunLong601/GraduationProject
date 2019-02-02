package com.hjl.graduationpro.Bluetooth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hjl.graduationpro.R;

import java.util.List;

public class BluetoothListAdapter extends ArrayAdapter<String> {

    private int resourceId;

    public BluetoothListAdapter(Context context, int textViewResourceId, @NonNull List<String> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position);
        View view ;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = view.findViewById(R.id.device_name_tv);
            view.setTag(viewHolder);

        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.deviceName.setText(name);
        Log.i("Adapter","getView");
        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    class ViewHolder {
        //ImageView Image;
        TextView deviceName;
    }

}
