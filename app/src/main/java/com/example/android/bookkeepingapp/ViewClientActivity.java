package com.example.android.bookkeepingapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ViewClientActivity extends AppCompatActivity {

    private String TAG = "ViewClientActivity";
    private Toolbar toolbar;
    private TextView mClientName;
    private TextView mCompany;
    private TextView mPhoneNumber;
    private TextView mEmail;
    private TextView mAddress;
    private TextView mCountry;
    private String extras;

    // Firebase instance variables
    private FirebaseUser user;
    private  String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClientDatabaseReference;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_client );


        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.clints_view_text));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        //enable back navigation icon for costume toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mClientDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "client" );
            //For offline sync of data
            mClientDatabaseReference.keepSynced(true);
        }

        mClientName = (TextView) findViewById( R.id.name_text_view );
        mCompany = (TextView) findViewById( R.id.company_text_view );
        mEmail = (TextView) findViewById( R.id.email_text_view );
        mAddress = (TextView) findViewById( R.id.address_text );
        mPhoneNumber = (TextView) findViewById( R.id.phone_text_view );
        mCountry = (TextView) findViewById( R.id.country_text_view );
        mClientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               Client client = new Client(  );
               client.setFirstName( dataSnapshot.child(extras).getValue(Client.class).getFirstName()); //set the name
                client.setLastName( dataSnapshot.child(extras).getValue(Client.class).getLastName()); //set the name
                if(client.getFirstName() != null || client.getLastName() != null) {
                    mClientName.setText( client.getFirstName() + " " + client.getLastName() );
                }
                client.setCompanyName( dataSnapshot.child(extras).getValue(Client.class).getCompanyName());

                if(client.getCompanyName() != null) {
                    mCompany.append( client.getCompanyName() );
                }
                client.setAddress( dataSnapshot.child(extras).getValue(Client.class).getAddress());
                if(client.getAddress() != null) {
                    mAddress.append( client.getAddress() );
                }
                client.setPhoneNumber( dataSnapshot.child(extras).getValue(Client.class).getPhoneNumber());
                if(client.getPhoneNumber() != null) {
                    mPhoneNumber.append( client.getPhoneNumber() );
                }
                client.setEmail( dataSnapshot.child(extras).getValue(Client.class).getEmail());
                if(client.getEmail() != null) {
                    mEmail.append( client.getEmail() );
                }
                client.setCountry( dataSnapshot.child(extras).getValue(Client.class).getCountry());
                if(client.getCountry() != null) {
                    mCountry.append( client.getCountry() );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        extras = getIntent().getStringExtra("clientId");
        if (extras != null) {
            //Show Client Name

        }

      /*  toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go back to the Client activity
                intent = new Intent( view.getContext(), MainActivity.class );
                intent.putExtra( "fragmentName", "clientFragment" );
               startActivity( intent );
            }
        });*/




        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    // User is signed out
                    //go to login activity
                    Intent i = new Intent(ViewClientActivity.this, LoginActivity.class);
                    startActivity(i);
                    toastMessage("Successfully signed out");
                }
            }
        };
    }



    //this is the physical back (on the actual phone)
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
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                //Go back to client fragment
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("fragmentName","clientFragment"); //for example
                startActivity(intent);
                return true;
            case R.id.action_delete:
                //your code here
                //Add dialog box
                Log.v(TAG, "Oops you just deleted me!");
                return true;
            case R.id.action_edit:
                //View edit activity
                //return the object in the list View
                String clientID = extras;
                Intent i = new Intent( ViewClientActivity.this, EditClientActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "clientId", clientID );
                startActivity( i );
                Log.v(TAG, "OK edit me now!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
