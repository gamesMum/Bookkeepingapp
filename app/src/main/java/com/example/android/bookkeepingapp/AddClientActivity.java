package com.example.android.bookkeepingapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddClientActivity extends AppCompatActivity {

    private  String TAG = "AddClientActivity";
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mCompanyEditText;
    private EditText mEmailEditText;
    private EditText mPhoneNumber;
    private Toolbar toolbar;


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

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);

        //Initialize xml element
        mFirstNameEditText = (EditText) findViewById( R.id.first_name_add );
        mLastNameEditText = (EditText) findViewById( R.id.last_name_add );
        mCompanyEditText = (EditText) findViewById( R.id.company_add );
        mEmailEditText = (EditText) findViewById( R.id.email_add );
        mPhoneNumber = (EditText) findViewById( R.id.phone_add );

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClientDatabaseReference = mFirebaseDatabase.getReference().child( "client" );



    }

    public void createNewClient() {
        //get the elements in the dialog
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        //....the rest of infos

        //Check if the user enterd either the first or the last name
        if (firstName.trim().length() > 0 || lastName.trim().length() > 0) {
            String key = mClientDatabaseReference.push().getKey();
            String company = mCompanyEditText.getText().toString();
           /*

            // get user input and set it to result
            // edit text
            Client client = new Client( firstName, lastName );
            mClientDatabaseReference.child( key ).child( "firstName" ).setValue( firstName );
            mClientDatabaseReference.child( key ).child( "lastName" ).setValue( lastName );*/

            Client client = new Client(key, firstName,lastName);
            mClientDatabaseReference.child(key).setValue(client);
            mClientDatabaseReference.child(key).child( "companyName" ).setValue(company);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                createNewClient();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}