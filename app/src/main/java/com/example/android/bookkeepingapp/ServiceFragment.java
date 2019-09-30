package com.example.android.bookkeepingapp;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {

    private static final String TAG = "ServiceFragment";

    //service xml attributes
    private ListView mServiceListView;
    private ServiceAdapter mServiceAdapter;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddServiceFab;
    private ChildEventListener mChildEventListener;
    private TextView mSearchBoxTextView;

    private FirebaseUser user;
    private  String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // Firebase instance variables

    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mServiceDatabaseReference;

    public ServiceFragment() {
        // Required empty public constructor
    }

    // Inflate the layout for this fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_service, container, false );

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mServiceDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "service" );
            //For offline sync of data
            mServiceDatabaseReference.keepSynced(true);
        }

        // Initialize references to views
        mProgressBar = (ProgressBar) rootView.findViewById( R.id.progressBar );
        mServiceListView = (ListView) rootView.findViewById( R.id.service_list_view );
        mAddServiceFab = (FloatingActionButton) rootView.findViewById( R.id.fab_service );
        mSearchBoxTextView = (TextView) rootView.findViewById( R.id.search_txt );

        // Initialize message ListView and its adapter
        final List<Service> services = new ArrayList<>();
        mServiceAdapter = new ServiceAdapter( getActivity(), R.layout.service_item, services );
        mServiceListView.setAdapter( mServiceAdapter );

        // Initialize progress bar
        mProgressBar.setVisibility( ProgressBar.VISIBLE );


        //Add new service
        mAddServiceFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to Add new service activity
                Intent i = new Intent( getActivity(), AddServiceActivity.class );
                startActivity( i );

            }
        } );

        //Add itemClickListener for each list item in the list view
        mServiceListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //return the object in the list View
                //Service service = services.get(position -  parent.getFirstVisiblePosition());
                Service service =(Service) parent.getItemAtPosition(position);
                Log.v(TAG, "the position is: " + String.valueOf(position -  parent.getFirstVisiblePosition()));
                Intent i = new Intent( getActivity(), ViewServiceActivity.class );
                //pass the service number to the next activity
                i.putExtra( "serviceNum", service.getServiceNum() );
                startActivity( i );
                Log.v(TAG, service.toString());
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

        // Capture Text in EditText
        mSearchBoxTextView.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
                // Call back the Adapter with current character to Filter
                mServiceAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

            mServiceDatabaseReference.removeEventListener( mChildEventListener );
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Service service = dataSnapshot.getValue( Service.class );
                    mServiceAdapter.add( service );
                    mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    if (!dataSnapshot.exists()) {
                        mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    }
                    Log.d( TAG + "Added", dataSnapshot.getValue( Service.class ).toString() );
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
            mServiceDatabaseReference.addChildEventListener( mChildEventListener );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle( R.string.service_text );
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }


}

