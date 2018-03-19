package com.example.android.bookkeepingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddClientActivity extends AppCompatActivity {

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mCompanyEditText;
    private EditText mEmailEditText;
    private EditText mPhoneNumber;

    private Button mCreateClientButton;

    private String clientID;

    //Firebase variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mClientDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_client );

        //Initialize xml element
        mFirstNameEditText = (EditText) findViewById( R.id.first_name_edit );
        mLastNameEditText = (EditText) findViewById( R.id.last_name_edit );
        mCompanyEditText = (EditText) findViewById( R.id.company_name_edit );
        mEmailEditText = (EditText) findViewById( R.id.email_edit );
        mPhoneNumber = (EditText) findViewById( R.id.phone_edit );

        mCreateClientButton = (Button) findViewById( R.id.creat_client_btn );
        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClientDatabaseReference = mFirebaseDatabase.getReference().child( "client" );

        mCreateClientButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewClient();

            }
        } );


    }

    public void createNewClient() {
        //get the elements in the dialog
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        //....the rest of infos

        //Check if the user enterd either the first or the last name
        if (firstName.trim().length() > 0 || lastName.trim().length() > 0) {
            String key = mClientDatabaseReference.push().getKey();
           /*

            // get user input and set it to result
            // edit text
            Client client = new Client( firstName, lastName );
            mClientDatabaseReference.child( key ).child( "firstName" ).setValue( firstName );
            mClientDatabaseReference.child( key ).child( "lastName" ).setValue( lastName );*/

            Client client = new Client(firstName,lastName);
            mClientDatabaseReference.child(key).setValue(client);
            toastMessage("New Client has been saved.");
            mFirstNameEditText.setText("");
            mLastNameEditText.setText("");

            //Go back to client fragment
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("fragmentName","clientFragment"); //for example
            startActivity(intent);

        } else {
            //else tell the user that there is an error
            toastMessage( "Enter customer name or company name" );
        }

    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Go back to client fragment
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("fragmentName","clientFragment"); //for example
        startActivity(intent);
    }
}
