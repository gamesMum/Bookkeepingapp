package com.example.android.bookkeepingapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvoicesFragment extends Fragment {

    private FloatingActionButton mAddInvoiceFab;

    public InvoicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_invoices, container, false );

        mAddInvoiceFab = (FloatingActionButton) rootView.findViewById( R.id.fab_invoice );

        mAddInvoiceFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to Add new invoice activity
                Intent i = new Intent( getActivity(), AddInvoiceActivity.class );
                startActivity( i );
            }
        } );
        return rootView;
    }

}
