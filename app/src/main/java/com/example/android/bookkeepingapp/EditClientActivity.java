package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditClientActivity extends AppCompatActivity {


    private  String TAG = "EditClientActivity";
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mCompanyEditText;
    private EditText mEmailEditText;
    private EditText mPhoneNumberEditText;
    private EditText mAddressEditText;
    private Spinner mCountrySpinner;
    private Toolbar toolbar;
    private Intent intent;

    private String extras;


    private String clientID;

    //Firebase variables
    private FirebaseUser user;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mClientDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_client );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);

        toolbar.setTitle(getString(R.string.clients_edit));
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        intent = new Intent(this, MainActivity.class);
        ActionBar actionbar = getSupportActionBar();

        //Initialize xml element
        mFirstNameEditText = (EditText) findViewById( R.id.first_name_edit );
        mLastNameEditText = (EditText) findViewById( R.id.last_name_edit );
        mCompanyEditText = (EditText) findViewById( R.id.company_edit );
        mEmailEditText = (EditText) findViewById( R.id.email_edit );
        mPhoneNumberEditText = (EditText) findViewById( R.id.phone_edit );
        mAddressEditText= (EditText) findViewById( R.id.address_edit );
        mCountrySpinner = (Spinner) findViewById( R.id.country_edit_spinner );

                        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            mClientDatabaseReference = mFirebaseDatabase.getReference().child( userID ).child( "client" );
        }

        //take the client Id selected from the previous activity
        extras = getIntent().getStringExtra("clientId");
        if (extras != null) {
            //Show Client Name

        }

        mClientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Client client = new Client(  );
                client.setFirstName( dataSnapshot.child(extras).getValue(Client.class).getFirstName()); //set the name
                client.setLastName( dataSnapshot.child(extras).getValue(Client.class).getLastName()); //set the name
                if(client.getFirstName() != null || client.getLastName() != null) {
                    mFirstNameEditText.setText( client.getFirstName());
                    mLastNameEditText.setText( client.getLastName());
                }
                client.setCompanyName( dataSnapshot.child(extras).getValue(Client.class).getCompanyName());

                if(client.getCompanyName() != null) {
                    mCompanyEditText.append( client.getCompanyName() );
                }
                client.setAddress( dataSnapshot.child(extras).getValue(Client.class).getAddress());
                if(client.getAddress() != null) {
                    mAddressEditText.append( client.getAddress() );
                }
                client.setPhoneNumber( dataSnapshot.child(extras).getValue(Client.class).getPhoneNumber());
                if(client.getPhoneNumber() != null) {
                    mPhoneNumberEditText.append( client.getPhoneNumber() );
                }
                client.setEmail( dataSnapshot.child(extras).getValue(Client.class).getEmail());
                if(client.getEmail() != null) {
                    mEmailEditText.append( client.getEmail() );
                }
                client.setCountry( dataSnapshot.child(extras).getValue(Client.class).getCountry());
                if(client.getCountry() != null) {
                    mCountrySpinner.setSelection(getIndex(mCountrySpinner, client.getCountry()));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void updaeClient() {
        //get the elements in the dialog
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String company = mCompanyEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String phone = mPhoneNumberEditText.getText().toString();
        String country = String.valueOf(  mCountrySpinner.getSelectedItem() );
        String address = mAddressEditText.getText().toString();


        //Check if the user enterd either the first or the last name
        if (firstName.trim().length() > 0 || lastName.trim().length() > 0) {
            //set the values at the same clientId selected (update)
            String key = extras;

            Client client = new Client(key, firstName,lastName);
            mClientDatabaseReference.child(key).setValue(client);
            mClientDatabaseReference.child(key).child( "companyName" ).setValue(company);
            mClientDatabaseReference.child(key).child( "email" ).setValue(email);
            mClientDatabaseReference.child(key).child( "phoneNumber" ).setValue(phone);
            mClientDatabaseReference.child(key).child( "address" ).setValue(address);
            mClientDatabaseReference.child(key).child( "country" ).setValue(country);

            toastMessage("data is up to date.");
            mFirstNameEditText.setText("");
            mLastNameEditText.setText("");
            mCompanyEditText.setText( "" );
            mPhoneNumberEditText.setText( "" );
            mCountrySpinner.setSelection( 0 );
            mAddressEditText.setText( "" );

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
                updaeClient();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}
