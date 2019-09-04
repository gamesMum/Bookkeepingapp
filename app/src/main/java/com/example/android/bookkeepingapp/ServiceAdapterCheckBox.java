package com.example.android.bookkeepingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by Rasha on 02/04/2018.
 */

public class ServiceAdapterCheckBox extends ArrayAdapter<Service> {

    private Hashtable<String, Integer> selectedServicesHash;
    private ArrayList<Boolean> status = new ArrayList<Boolean>();


    public ServiceAdapterCheckBox(@NonNull Context context, int resource, List<Service> objects
            , Hashtable<String, Integer> selectedServiceHash) {
        super( context, resource, objects );
        this.selectedServicesHash = selectedServiceHash;
    }

    public void setSelectedServicesHash(Hashtable<String, Integer> selectedServices) {
        this.selectedServicesHash = selectedServices;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Find the current object
        final Service service = getItem(position);
        //apply View holder to optimize the listView
        //create a ViewHolder reference
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_item_check_box,
                    parent, false);

            holder.textCheckBox = (CheckedTextView) convertView.findViewById(R.id.checkedTextView);
            holder.minBtn = (Button) convertView.findViewById( R.id.minus_btn );
            holder.plusBtn = (Button) convertView.findViewById( R.id.plus_btn );
            holder.quantityTv = (TextView) convertView.findViewById( R.id.quantity_tv );
            convertView.setTag(holder);
           // convertView.setTag(R.id.checkedTextView, holder);
        }
        else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
            Log.v("service Adapter", "the hash table in the adapter contains:" + selectedServicesHash.toString());
            Log.v("service Adapter", "the hash table in the adapter contains:"
                    + selectedServicesHash.toString());

        }

        //supply the textViews with the correct data
        assert service != null;
        holder.textCheckBox.setText( service.getServiceName() );
        // ALSO UPDATE THE CHECKED STATE - FROM YOUR POSTED CODE, I ASSUME IT SHOULD BE
        // CHECKED IF IT APPEARS IN THE "selectedServices" list

        //the service is in the selected services table
        if(selectedServicesHash.containsKey(service.getServiceNum())) {
            //check the box
            holder.textCheckBox.setChecked( true );
            holder.minBtn.setVisibility( View.VISIBLE );
            holder.plusBtn.setVisibility( View.VISIBLE );
            holder.quantityTv.setVisibility( View.VISIBLE );
            holder.quantityTv.setText(  selectedServicesHash.get( service.getServiceNum()).toString());

        }else
        {
            holder.textCheckBox.setChecked( false );
            holder.minBtn.setVisibility( View.INVISIBLE );
            holder.plusBtn.setVisibility( View.INVISIBLE );
            holder.quantityTv.setVisibility( View.INVISIBLE );
            holder.quantityTv.setText("1");

        }



        holder.plusBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.plusBtn.setFocusable( true );
                int quantityValue = Integer.parseInt(holder.quantityTv.getText().toString());
                quantityValue++;
                holder.quantityTv.setText( String.valueOf( quantityValue ) );
                //update the value of the selectedServicesHash list
                selectedServicesHash.put(  service.getServiceNum(), Integer.valueOf(holder.quantityTv.getText().toString()));


            }
        } );

        holder.minBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.plusBtn.setFocusable( true );
                int quantityValue = Integer.parseInt(holder.quantityTv.getText().toString());
                if(quantityValue == 1){
                    quantityValue = 1;
                }
                else {
                    quantityValue--;
                }
                holder.quantityTv.setText( String.valueOf( quantityValue ) );
                //update the value of the selectedServicesHash list
                selectedServicesHash.put(  service.getServiceNum(), Integer.valueOf(holder.quantityTv.getText().toString()));


            }
        } );


        return  convertView;
    }

    public Hashtable<String, Integer> getSelectedServicesHash() {
        return selectedServicesHash;
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText( getContext(), message, Toast.LENGTH_SHORT ).show();
    }

    private class ViewHolder {

        protected CheckedTextView textCheckBox;
        protected  Button plusBtn;
        protected  Button minBtn;
        protected TextView quantityTv;

    }
}

