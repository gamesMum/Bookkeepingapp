package com.example.android.bookkeepingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class ProfitAdapter extends ArrayAdapter<Profit> {
    public ProfitAdapter(@NonNull Context context, int resource, List<Profit> objects) {
        super( context, resource, objects );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profit_item,
                    parent, false);
        }

        //Find the current object
        final Profit profit = getItem(position);
        //Find the First and Last name TextViews
        TextView profitName = (TextView) convertView.findViewById(R.id.profit_name_tv_item);
        TextView profitValue = (TextView) convertView.findViewById(R.id.profit_value_tv_item);


        //supply the textViews with the correct data
        assert profit != null;
        profitName.setText(profit.getProfitName());
        profitValue.setText( "₺"+ profit.getProfitValue());
        //Check if there is a company name to display
       /* if(client.getmCompanyName().toString().trim().length() > 0) {
            companyName.setVisibility(View.VISIBLE);

        }else {
            companyName.setVisibility(View.GONE);
        }*/

        return convertView;

    }
}
