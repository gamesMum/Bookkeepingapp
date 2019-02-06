package com.example.android.bookkeepingapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummeryFragment extends Fragment {

    private double totalProfit;
    private double totalExpenses;
    private TextView profitText;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    // Firebase instance variables
    private FirebaseUser user;
    private  String userID;

    public SummeryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_summery, container, false );

        profitText = (TextView) rootView.findViewById( R.id.profit_txt );

        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "user" );
            //For offline sync of data
            mUserDatabaseReference.keepSynced(true);
        }

        //get current profit for the user
        mUserDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               /* User userApp = new User(  );
                //select and calculate the sum of all invoices'  profits
                //select the sum of all expenses for invoces and others
                //get the profit for the current user
                userApp.setTotalProfit( dataSnapshot.getValue(User.class).getProfit()); //set the name
                totalProfit = userApp.getProfit();
                //set the text view with the current profit value for the user
                profitText.setText( String.valueOf( totalProfit )  );*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


        // Inflate the layout for this fragment
        return rootView;
    }

}
