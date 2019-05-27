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

public class ExpenseAdapter extends ArrayAdapter<Expense> {
    public ExpenseAdapter(@NonNull Context context, int resource, List<Expense> objects) {
        super( context, resource, objects );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_item,
                    parent, false);
        }

        //Find the current object
        final Expense expense = getItem(position);
        //Find the First and Last name TextViews
        TextView expenseName = (TextView) convertView.findViewById(R.id.expense_name_tv_item);
        TextView expenseValue = (TextView) convertView.findViewById(R.id.expense_value_tv_item);


        //supply the textViews with the correct data
        assert expense != null;
        expenseName.setText(expense.getExpenseName());
        expenseValue.setText( "₺"+ expense.getExpenseValue());
        //Check if there is a company name to display
       /* if(client.getmCompanyName().toString().trim().length() > 0) {
            companyName.setVisibility(View.VISIBLE);

        }else {
            companyName.setVisibility(View.GONE);
        }*/

        return convertView;

    }
}
