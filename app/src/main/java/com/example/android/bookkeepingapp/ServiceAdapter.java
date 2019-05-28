package com.example.android.bookkeepingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rasha on 30/03/2018.
 */

public class ServiceAdapter extends ArrayAdapter<Service> {
    public ServiceAdapter(@NonNull Context context, int resource, List<Service> objects) {
        super( context, resource, objects );
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_item,
                    parent, false);
        }

        //Find the current object
        final Service service = getItem(position);
        //Find the First and Last name TextViews
        TextView serviceName = (TextView) convertView.findViewById(R.id.service_name_text_view_item);
        TextView servicePrice = (TextView) convertView.findViewById(R.id.service_price_text_view_item);


        //supply the textViews with the correct data
        assert service != null;
        serviceName.setText(service.getServiceName());
        //format the price in the label as(2,000,000)
        DecimalFormat formatter = new DecimalFormat( "##,###,###" );
        String FormatString = formatter.format( service.getServicePrice() );
        servicePrice.setText( "₺"+ String.valueOf(FormatString));
        //Check if there is a company name to display
       /* if(client.getmCompanyName().toString().trim().length() > 0) {
            companyName.setVisibility(View.VISIBLE);

        }else {
            companyName.setVisibility(View.GONE);
        }*/

        return convertView;

    }
}
