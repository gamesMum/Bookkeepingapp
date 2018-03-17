package com.example.android.bookkeepingapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * adapter for the Client listView
 */
public class ClientAdapter extends ArrayAdapter<Client> {

     ClientAdapter(Context context, int resource, List<Client> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.client_item, parent, false);
        }
        //Find the current object
        Client client = getItem(position);
        //Find the First and Last name TextViews
        TextView firstName = (TextView) convertView.findViewById(R.id.first_name_text);
        TextView lastName = (TextView) convertView.findViewById(R.id.last_name_text);
       // TextView companyName = (TextView) convertView.findViewById(R.id.company_name_text);

        //supply the textViews with the correct data
           // firstName.setText("Hello");

            firstName.setText(client.getFirstName());
            lastName.setText(client.getLastName());


        //Check if there is a company name to display
       /* if(client.getmCompanyName().toString().trim().length() > 0) {
            companyName.setVisibility(View.VISIBLE);
            companyName.setText(client.getmCompanyName());
        }else {
            companyName.setVisibility(View.GONE);
        }*/

        return convertView;
    }
}
