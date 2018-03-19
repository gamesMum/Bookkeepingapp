package com.example.android.bookkeepingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by Rasha on 18/03/2018.
 */

public class InvoiceAdapter extends ArrayAdapter<Invoice> {
    public InvoiceAdapter(@NonNull Context context, int resource, @NonNull Invoice[] objects) {
        super( context, resource, objects );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView( position, convertView, parent );
    }
}
