package com.example.android.bookkeepingapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;


/**
 * Created by Rasha on 14/03/2018.
 */
public class ClientAdapter extends ArrayAdapter<Client> {
    public ClientAdapter(@NonNull Context context, int resource, List<Client> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.client_item, parent, false);
        }
        //Find the current object
        Client client = getItem(position);
        //Do the rest
        return convertView;
    }
}
