package com.example.android.bookkeepingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Rasha on 02/04/2018.
 */

public class ServiceAdapterCheckBox extends BaseAdapter  implements Filterable {

    private Hashtable<String, Integer> selectedServicesHash;
    private ArrayList<Boolean> status = new ArrayList<Boolean>();
    private List<Service> mOriginalValues; // Original Values
    private List<Service> mDisplayedValues;    // Values to be displayed
    LayoutInflater inflater;

    private Context context = null;


    public ServiceAdapterCheckBox(@NonNull Context context, int resource, List<Service> mServiceList
            , Hashtable<String, Integer> selectedServiceHash) {
        this.mOriginalValues = mServiceList;
        this.mDisplayedValues = mServiceList;
        inflater = LayoutInflater.from( context );
        this.selectedServicesHash = selectedServiceHash;
    }
    void add(Service list)
    {
        this.mOriginalValues.add(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public Object getItem(int position) {
        return  mDisplayedValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedServicesHash(Hashtable<String, Integer> selectedServices) {
        this.selectedServicesHash = selectedServices;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //apply View holder to optimize the listView
        //create a ViewHolder reference
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate( R.layout.service_item_check_box, null );
            holder.llContainer = (LinearLayout) convertView.findViewById( R.id.llContainer_service_check_box );
            holder.textCheckBox = (CheckedTextView) convertView.findViewById(R.id.checkedTextView);
            holder.minBtn = (Button) convertView.findViewById( R.id.minus_btn );
            holder.plusBtn = (Button) convertView.findViewById( R.id.plus_btn );
            holder.quantityTv = (TextView) convertView.findViewById( R.id.quantity_tv );
            convertView.setTag(holder);
            context = parent.getContext();
           // convertView.setTag(R.id.checkedTextView, holder);
        }
        else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        //supply the textViews with the correct data
        holder.textCheckBox.setText(  mDisplayedValues.get( position ).getServiceName() );
        // ALSO UPDATE THE CHECKED STATE - FROM YOUR POSTED CODE, I ASSUME IT SHOULD BE
        // CHECKED IF IT APPEARS IN THE "selectedServices" list

        //the service is in the selected services table
        if(selectedServicesHash.containsKey(
                mDisplayedValues.get( position ).getServiceNum())) {
            //check the box
            holder.textCheckBox.setChecked( true );
            holder.minBtn.setVisibility( View.VISIBLE );
            holder.plusBtn.setVisibility( View.VISIBLE );
            holder.quantityTv.setVisibility( View.VISIBLE );
            holder.quantityTv.setText(  selectedServicesHash.get(
                    mDisplayedValues.get( position ).getServiceNum()).toString());

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
                selectedServicesHash.put(   mDisplayedValues.get( position ).getServiceNum(),
                        Integer.valueOf(holder.quantityTv.getText().toString()));


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
                selectedServicesHash.put(   mDisplayedValues.get( position ).getServiceNum(),
                        Integer.valueOf(holder.quantityTv.getText().toString()));


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
        Toast.makeText(context , message, Toast.LENGTH_SHORT ).show();
    }

    private class ViewHolder {
        LinearLayout llContainer;
        protected CheckedTextView textCheckBox;
        protected  Button plusBtn;
        protected  Button minBtn;
        protected TextView quantityTv;

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<Service>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Service> FilteredArrList = new ArrayList<Service>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Service>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getServiceName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Service(mOriginalValues.get(i).getServiceNum(),
                                    mOriginalValues.get(i).getServiceName(),mOriginalValues.get(i).getServicePrice(),
                                    mOriginalValues.get(i).getServicePriceSecCurrency()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}

