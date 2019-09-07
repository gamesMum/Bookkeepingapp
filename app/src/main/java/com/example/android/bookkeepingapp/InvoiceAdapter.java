package com.example.android.bookkeepingapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
            convertView = LayoutInflater.from( getContext() ).inflate( R.layout.invoice_item, parent, false );
        }
        //Find the current object
        final Invoice invoice = getItem( position );
        //Find the First and Last name TextViews
        TextView date = (TextView) convertView.findViewById( R.id.date_text );
        TextView total = (TextView) convertView.findViewById( R.id.total_text );
        TextView name = (TextView) convertView.findViewById( R.id.name_text );
        ImageView labelImage = (ImageView) convertView.findViewById( R.id.invoice_label_image );

        //call th user name
        
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy / MM / dd " );
        assert invoice != null;
        String invoiceDueDate = invoice.getDueDate();
        double invoiceStatus = invoice.getInvoiceStatus();

        date.setText( "Due Date: " + invoiceDueDate );


        Date dueDate = null;
        try {
            dueDate = sdf.parse( invoice.getDueDate() );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = null;
        try {
            currentDate = sdf.parse( getCurrentDate() );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //if thew invoice is paid show it in green
        if (invoice.getInvoiceStatus() == 1) {
            labelImage.setImageResource( R.drawable.green_sq );
        }
        //else check the due date
        //show it in yelow if it is still time
        else if (dueDate.getTime() >= currentDate.getTime()) {
            labelImage.setImageResource( R.drawable.yellow_sq );
        }
        //or red if it is ove due
        else {
            labelImage.setImageResource( R.drawable.red_sq );
        }
        Log.v("invoiceAdapter", "the invoice status: "+ invoiceStatus);


        // if(invoice.getDueDate() getCurrentDate() )
        //supply the textViews with the correct data
        assert invoice != null;
        String strInvoiceTotal = formatPrice(invoice.getTotal() );
        total.setText( "ID" + strInvoiceTotal );

        return convertView;
    }
    private String formatPrice(double price){
        //format the price in the label as(2,000,000)
        DecimalFormat formatter = new DecimalFormat( "##,###,###" );
        String priceFormatted = formatter.format( price );
        return priceFormatted;
}

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat( "yyyy / MM / dd " );
        String strDate = mdformat.format( calendar.getTime() );
        return strDate;
    }
}
