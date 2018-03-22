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
    private EditText mPhoneNumber;
    private Toolbar toolbar;

    private String extras;


    private String clientID;

    //Firebase variables
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

        //Initialize xml element
        mFirstNameEditText = (EditText) findViewById( R.id.first_name_edit );
        mLastNameEditText = (EditText) findViewById( R.id.last_name_edit );
        mCompanyEditText = (EditText) findViewById( R.id.company_edit );
        mEmailEditText = (EditText) findViewById( R.id.email_edit );
        mPhoneNumber = (EditText) findViewById( R.id.phone_edit );

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClientDatabaseReference = mFirebaseDatabase.getReference().child( "client" );

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
                client.setCompanyName( dataSnapshot.child(extras).getValue(Client.class).getCompanyName()); //set the name
                mFirstNameEditText.setText( client.getFirstName());
                mLastNameEditText.setText( client.getLastName());
                //set the rest of data
                mCompanyEditText.setText( client.getCompanyName() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updaeClient() {
        //get the elements in the dialog
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String company = mCompanyEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String phone = mPhoneNumber.getText().toString();
        //....the rest of infos

        //Check if the user enterd either the first or the last name
        if (firstName.trim().length() > 0 || lastName.trim().length() > 0) {
            //set the values at the same clientId selected (update)
            String key = extras;
           /*

            // get user input and set it to result
            // edit text
            Client client = new Client( firstName, lastName );
            mClientDatabaseReference.child( key ).child( "firstName" ).setValue( firstName );
            mClientDatabaseReference.child( key ).child( "lastName" ).setValue( lastName );*/

            Client client = new Client(key, firstName,lastName);
            mClientDatabaseReference.child(key).setValue(client);
            toastMessage("data is up to date.");
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
                updaeClient();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
