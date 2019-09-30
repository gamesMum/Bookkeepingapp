package com.example.android.bookkeepingapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rasha on 30/03/2018.
 */

public class ServiceAdapter extends BaseAdapter implements Filterable {
    //Declare variables
    private List<Service> mOriginalValues; // Original Values
    private List<Service> mDisplayedValues;    // Values to be displayed
    LayoutInflater inflater;


    public ServiceAdapter(@NonNull Context context, int resource, List<Service> mServiceList) {
        this.mOriginalValues = mServiceList;
        this.mDisplayedValues = mServiceList;
        inflater = LayoutInflater.from( context );
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
        return mDisplayedValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        RelativeLayout llContainer;
        CardView lllContainer;
        TextView tvName, tvPrice;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate( R.layout.service_item, null );
            holder.llContainer = (RelativeLayout) convertView.findViewById( R.id.llContainer_service_item );
            holder.lllContainer = (CardView) convertView.findViewById( R.id.lllContainer_card_view );
            holder.llContainer = (RelativeLayout) convertView.findViewById( R.id.llContainer_service_item );
            holder.tvName = (TextView) convertView.findViewById( R.id.service_name_text_view_item );
            holder.tvPrice = (TextView) convertView.findViewById( R.id.service_price_text_view_item );
            convertView.setTag( holder );
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Find the current object
        holder.tvName.setText( mDisplayedValues.get( position ).getServiceName() );
        holder.tvPrice.setText( "₺" + mDisplayedValues.get( position ).getServicePrice() + "" );

        //format the price in the label as(2,000,000)
        DecimalFormat formatter = new DecimalFormat( "##,###,###" );
        // String FormatString = formatter.format( service.getServicePrice() );
        //Check if there is a company name to display
       /* if(client.getmCompanyName().toString().trim().length() > 0) {
            companyName.setVisibility(View.VISIBLE);

        }else {
            companyName.setVisibility(View.GONE);
        }*/

        return convertView;

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
