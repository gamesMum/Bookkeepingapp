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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfitFragment extends Fragment {
    private static final String TAG = "ProfitFragment";

    //expense xml attributes
    private ListView mProfitListView;
    private ProfitAdapter mProfitAdapter;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddProfitFab;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ChildEventListener mChildEventListener;


    private FirebaseUser user;
    private  String userID;
    // Firebase instance variables
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProfitDatabaseReference;
    public ProfitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_profit, container, false );

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mProfitDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "profit" );
            //For offline sync of data
            mProfitDatabaseReference.keepSynced(true);
        }

        // Initialize references to views
        mProgressBar = (ProgressBar) rootView.findViewById( R.id.profit_progressBar );
        mProfitListView = (ListView) rootView.findViewById( R.id.profit_list_view );
        mAddProfitFab = (FloatingActionButton) rootView.findViewById( R.id.profit_fab);

        // Initialize message ListView and its adapter
        final List<Profit> profits = new ArrayList<>();
        mProfitAdapter = new ProfitAdapter( getActivity(), R.layout.profit_item, profits );
        mProfitListView.setAdapter( mProfitAdapter );

        // Initialize progress bar
        mProgressBar.setVisibility( ProgressBar.VISIBLE );


        //Add new expense
        mAddProfitFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to Add new expense activity
                Intent i = new Intent( getActivity(), AddProfitActivity.class );
                startActivity( i );

            }
        } );

        mProfitListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //return the object in the list View
                Profit profit = profits.get( pos );
                Intent in = new Intent( getActivity(), ViewProfitActivity.class );
                //pass the expense number to the next activity
                in.putExtra( "ProfitNum", profit.getProfitNum() );
                startActivity( in );
                Log.v(TAG, profit.toString());
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
        // attachDatabaseReadListener();
    }



    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        Log.v(TAG, TAG + " on pause");
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {

            mProfitDatabaseReference.removeEventListener( mChildEventListener );
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Profit profit = dataSnapshot.getValue( Profit.class );
                    mProfitAdapter.add( profit );
                    mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    if (!dataSnapshot.exists()) {
                        mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    }
                    Log.d( TAG + "Added", dataSnapshot.getValue( Expense.class ).toString() );
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
            mProfitDatabaseReference.addChildEventListener( mChildEventListener );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle( R.string.profit_text );
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }


}
