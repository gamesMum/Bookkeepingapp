package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddInvoiceActivity extends AppCompatActivity {

    private  String TAG = "AddInvoiceActivity";
    //invoice xml elements
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mCompanyEditText;
    private EditText mEmailEditText;
    private EditText mPhoneNumber;
    private Toolbar toolbar;


    private String clientID;

    //Firebase variables
    private FirebaseUser user;
    private  String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mClientDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_invoice );
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
        }
        //store the data under loggedin user Id
        mClientDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "invoice" );


    }
    public void createNewInvoice(String clientID, ArrayList<String> serviceIds) {
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

            Invoice invoice = new Invoice(clientID, serviceIds);
            mClientDatabaseReference.child(key).setValue(invoice);
            //mClientDatabaseReference.child(key).child( "companyName" ).setValue(company);
            toastMessage("New Invoice has been saved.");
            mFirstNameEditText.setText("");
            mLastNameEditText.setText("");

            //Go back to client fragment
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("fragmentName","invoiceFragment");
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
                //createNewInvoice("", {"",""});
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
