package com.example.android.bookkeepingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView( position, convertView, parent );
    }
}
