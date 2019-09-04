package com.example.android.bookkeepingapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

         //apply View holder to optimize the listView
        //create a ViewHolder reference
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.client_item,
                    parent, false);
            // get all views you need to handle from the cell and save them in the view holder
            holder.clientName = (TextView) convertView.findViewById(R.id.name_text_view_item);
            holder.companyName = (TextView) convertView.findViewById(R.id.company_name_text_view_item);

            // save the view holder on the cell view to get it back latter
            convertView.setTag(holder);
        }
        else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }
        //Find the current object
        final Client client = getItem(position);
        String clientFirstName = client.getFirstName();
        String clientLastName = client.getLastName();
        String clientCompanyName = client.getCompanyName();
        if(clientFirstName != null || clientLastName != null) {
            //supply the textViews with the correct data
            assert client != null;
            holder.clientName.setText( clientFirstName + " " + clientLastName );
            holder.companyName.setText( clientCompanyName );
        }
        return convertView;
    }

    /**
     * Used to avoid calling "findViewById" every time the getView() method is called,
     * because this can impact to your application performance when your list is large
     */
    private class ViewHolder {

        protected TextView clientName;
        protected TextView companyName;

    }
}
