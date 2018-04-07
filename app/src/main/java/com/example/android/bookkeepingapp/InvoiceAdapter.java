package com.example.android.bookkeepingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rasha on 18/03/2018.
 */

public class InvoiceAdapter extends ArrayAdapter<Invoice> {

    private ArrayList<String> serviceIds;

    public InvoiceAdapter(Context context, int resource, List<Invoice> objects) {
        super( context, resource, objects );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.invoice_item,
                    parent, false);
        }
        //Find the current object
        final Invoice invoice = getItem(position);
        //Find the First and Last name TextViews
        //TextView name = (TextView) convertView.findViewById(R.id.name_text_view_item);
        TextView total = (TextView) convertView.findViewById(R.id.total_text);

        //supply the textViews with the correct data
        assert invoice != null;
       total.setText( "₺" + String.valueOf( invoice.getTotal() ));

        return convertView;
    }
}
