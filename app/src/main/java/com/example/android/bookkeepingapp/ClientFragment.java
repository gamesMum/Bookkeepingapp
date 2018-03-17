package com.example.android.bookkeepingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    // Firebase instance variables
    public  FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClientDatabaseReference;

    public ClientFragment() {
        // Required empty public constructor
    }

    // Inflate the layout for this fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //find and inflate view
        final View rootView = inflater.inflate(R.layout.fragment_client, container, false);

        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClientDatabaseReference = mFirebaseDatabase.getReference().child("client");

        // Initialize references to views
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mClientListView = (ListView) rootView.findViewById(R.id.client_list_view);
        mAddUserFab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        // Initialize message ListView and its adapter
        List<Client> clients = new ArrayList<>();
        mClientAdapter = new ClientAdapter(getActivity(), R.layout.client_item, clients);
        mClientListView.setAdapter( mClientAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);


        //Add new client
        mAddUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show new client dialog when we press FAB to add new client
                //and Add new client
                createNewClient();
            }
        });

            mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
             Client client = dataSnapshot.getValue(Client.class);
             mClientAdapter.add(client);
             mProgressBar.setVisibility(View.INVISIBLE);
                Log.d(TAG+"Added",dataSnapshot.getValue(Client.class).toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG+"Changed",dataSnapshot.getValue(Client.class).toString());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG+"Removed",dataSnapshot.getValue(Client.class).toString());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG+"Moved",dataSnapshot.getValue(Client.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"Cancelled",databaseError.toString());
            }
        };

        mClientDatabaseReference.addChildEventListener(mChildEventListener);

        return rootView;

    }

    public void createNewClient() {
        //inflate the xml and display the dialog box
        LayoutInflater li = LayoutInflater.from(getActivity());
        View getNewClientItem = li.inflate(R.layout.new_client_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(getNewClientItem);

        //get the elements in the dialog
        final EditText firstNameEditText = (EditText) getNewClientItem.findViewById(R.id.first_name_edit);
        final EditText lastNameEditText = (EditText) getNewClientItem.findViewById(R.id.last_name_edit);
        final EditText companytNameEditText = (EditText) getNewClientItem.findViewById(R.id.company_name_edit);
        final EditText phoneEditText = (EditText) getNewClientItem.findViewById(R.id.phone_edit);
        final EditText emailEditText = (EditText) getNewClientItem.findViewById(R.id.email_edit);
        final EditText addressEditText = (EditText) getNewClientItem.findViewById(R.id.address_edit);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                       String key =  mClientDatabaseReference.push().getKey();

                        // get user input and set it to result
                        // edit text
                        Client client = new Client(firstNameEditText.getText().toString(),
                                lastNameEditText.getText().toString());
                        mClientDatabaseReference.child(key).child("firsttName")
                                .setValue(firstNameEditText.getText().toString());
                        mClientDatabaseReference.child(key).child("lastName")
                                .setValue(lastNameEditText.getText().toString());


                    }
                }).create()
                .show();

    }

    @Override
    public void onResume() {
        super.onResume();
        mClientDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mClientDatabaseReference.removeEventListener(mChildEventListener);
    }
}
