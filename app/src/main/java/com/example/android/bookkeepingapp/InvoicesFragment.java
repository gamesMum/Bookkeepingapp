package com.example.android.bookkeepingapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvoicesFragment extends Fragment {

    private static final String TAG = "InvoicesFragment";

    //client xml attributes
    private ListView mInvoiceListView;
    private InvoiceAdapter mInvoiceAdapter;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddUserFab;
    private ChildEventListener mChildEventListener;

    private FirebaseUser user;
    private  String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // Firebase instance variables
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mInvoiceDatabaseReference;

    public InvoicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_invoices, container, false );

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mInvoiceDatabaseReference = mFirebaseDatabase.getReference().
                    child( userID ).child( "invoice" );
            //For offline sync of data
            mInvoiceDatabaseReference.keepSynced(true);
        }

        // Initialize references to views
        mProgressBar = (ProgressBar) rootView.findViewById( R.id.progressBar );
        mInvoiceListView = (ListView) rootView.findViewById( R.id.invoices_list_view );
        mAddUserFab = (FloatingActionButton) rootView.findViewById( R.id.fab_invoice );

        // Initialize message ListView and its adapter
        final List<Invoice> invoices = new ArrayList<>();
        mInvoiceAdapter = new InvoiceAdapter( getActivity(), R.layout.invoice_item, invoices );
        mInvoiceListView.setAdapter( mInvoiceAdapter );

        // Initialize progress bar
        mProgressBar.setVisibility( ProgressBar.VISIBLE );


        //Add new client
        mAddUserFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to Add new client activity
                //go to login activity
                Intent i = new Intent( getActivity(), AddInvoiceActivity.class );
                startActivity( i );

            }
        } );

        //Add itemClickListener for each list item in the list view
        mInvoiceListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //return the object in the list View
                Invoice invoice = invoices.get( position );
                Intent i = new Intent( getActivity(), ViewInvoiceActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "invoiceNum", invoice.getInvoiceNumber() );
                startActivity( i );
                Log.v(TAG, invoice.toString());
            }
        } );

        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (user == null) {

                    // User is signed out
                    //go to login activity
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    toastMessage("Successfully signed out");
                }
            }
        };

        //Attach the onChildAdded listener only when the activity created
        //no duplicates
        attachDatabaseReadListener();
        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, TAG + " is resumed");
        //attachDatabaseReadListener();
    }



    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        Log.v(TAG, TAG + " on pause");
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {

            mInvoiceDatabaseReference.removeEventListener( mChildEventListener );
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Invoice invoice = dataSnapshot.getValue( Invoice.class );
                    mInvoiceAdapter.add( invoice );
                    mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    if (!dataSnapshot.exists()) {
                        mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    }
                    Log.d( TAG + "Added", dataSnapshot.getValue( Invoice.class ).toString() );
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mInvoiceDatabaseReference.addChildEventListener( mChildEventListener );
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }


}
