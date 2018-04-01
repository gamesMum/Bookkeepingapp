package com.example.android.bookkeepingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Use the {@link ClientFragment} factory method to
 * create an instance of this fragment.
 */
public class ClientFragment extends Fragment {

    private static final String TAG = "ClientFragment";

    //client xml attributes
    private ListView mClientListView;
    private ClientAdapter mClientAdapter;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddUserFab;
    private ChildEventListener mChildEventListener;

    private  FirebaseUser user;
    private  String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // Firebase instance variables
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClientDatabaseReference;

    public ClientFragment() {
        // Required empty public constructor
    }

    // Inflate the layout for this fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_client, container, false );

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mClientDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "client" );
            //For offline sync of data
            mClientDatabaseReference.keepSynced(true);
        }

        // Initialize references to views
        mProgressBar = (ProgressBar) rootView.findViewById( R.id.progressBar );
        mClientListView = (ListView) rootView.findViewById( R.id.client_list_view );
        mAddUserFab = (FloatingActionButton) rootView.findViewById( R.id.fab );

        // Initialize message ListView and its adapter
        final List<Client> clients = new ArrayList<>();
        mClientAdapter = new ClientAdapter( getActivity(), R.layout.client_item, clients );
        mClientListView.setAdapter( mClientAdapter );

        // Initialize progress bar
        mProgressBar.setVisibility( ProgressBar.VISIBLE );


        //Add new client
        mAddUserFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to Add new client activity
                //go to login activity
                Intent i = new Intent( getActivity(), AddClientActivity.class );
                startActivity( i );

            }
        } );

        //Add itemClickListener for each list item in the list view
        mClientListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //return the object in the list View
                Client client = clients.get( position );
                Intent i = new Intent( getActivity(), ViewClientActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "clientId", client.getClientId() );
                startActivity( i );
                Log.v(TAG, client.toString());
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

            mClientDatabaseReference.removeEventListener( mChildEventListener );
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Client client = dataSnapshot.getValue( Client.class );
                    mClientAdapter.add( client );
                    mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    if (!dataSnapshot.exists()) {
                        mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    }
                    Log.d( TAG + "Added", dataSnapshot.getValue( Client.class ).toString() );
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
            mClientDatabaseReference.addChildEventListener( mChildEventListener );
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
