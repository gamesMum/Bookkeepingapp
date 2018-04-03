package com.example.android.bookkeepingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by Rasha on 02/04/2018.
 */

public class ServiceAdapterCheckBox extends ArrayAdapter<Service> {

    public ServiceAdapterCheckBox(@NonNull Context context, int resource, List<Service> objects) {
        super( context, resource, objects );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_item_check_box,
                    parent, false);
        }

        //Find the current object
        final Service service = getItem(position);
        CheckBox serviceName = (CheckBox) convertView.findViewById( R.id.service_check_box );

        //supply the textViews with the correct data
        assert service != null;
        serviceName.setText( service.getServiceName() );
        return  convertView;
    }
}
