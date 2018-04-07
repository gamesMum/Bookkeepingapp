package com.example.android.bookkeepingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rasha on 02/04/2018.
 */

public class ServiceAdapterCheckBox extends ArrayAdapter<Service> {

    private  ArrayList<String> selectedServices;

    public ServiceAdapterCheckBox(@NonNull Context context, int resource, List<Service> objects
            , ArrayList<String> selectedServices) {
        super( context, resource, objects );
        this.selectedServices = selectedServices;
    }

    public void setSelectedServices(ArrayList<String> selectedServices) {
        this.selectedServices = selectedServices;
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
        CheckedTextView serviceName = (CheckedTextView) convertView.findViewById( R.id.checkedTextView );

        //supply the textViews with the correct data
        assert service != null;

        serviceName.setText( service.getServiceName() );
       // ((ListView) parent).setItemChecked(position, true);

      /* ((ListView) parent).setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (String s : selectedServices) {
                    Log.v("AdapterXXX", "the list" + s);
                   *//* if () {
                        //store the selescted services numbers in arrayList
                        selectedServices.add( service.getServiceNum() );
                        toastMessage( service.getServiceNum() + " is added" );
                    } else {

                        //remove that object
                        selectedServices.remove( service.getServiceNum() );
                        toastMessage( service.getServiceNum() + " is removed" );
                    }
                    // ((ListView) parent).setItemChecked(position, true);*//*
                }
            }
        } );*/

        //iterate through the list of selected services
        //check if it is already selected
        //mark it as checked
        if(selectedServices.size() != 0) {
            for (String s : selectedServices) {
                if (service.getServiceNum().equals( s )) {
                    //set the check text with the current position to true
                    ((ListView) parent).setItemChecked( position, true );
                }
            }
        }

        return  convertView;
    }

    public ArrayList<String> getSelectedServices() {
        return selectedServices;
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText( getContext(), message, Toast.LENGTH_SHORT ).show();
    }
}
