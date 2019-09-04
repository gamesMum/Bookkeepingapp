package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditServiceActivity extends AppCompatActivity {


    private String TAG = "EditServiceActivity";
    private Toolbar toolbar;
    private TextView mServiceName;
    private TextView mServicePrice;
    private TextView mServiceNotes;
    private TextView mServicePriceSecCurrency;
    private String extras;
    //I need to get this via service
    private static double secondaryToPrimaryRate = 203.518;

    // Firebase instance variables
    private FirebaseUser user;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mServiceDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_service );


        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById( R.id.toolbar_edit_service );
        setSupportActionBar( toolbar );
        toolbar.setTitle( getString( R.string.service_edit ) );
        toolbar.setNavigationIcon( R.drawable.ic_close_black_24dp );

        ActionBar actionbar = getSupportActionBar();

        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mServiceDatabaseReference = mFirebaseDatabase.getReference().child( userID ).child( "service" );
            //For offline sync of data
            mServiceDatabaseReference.keepSynced( true );
        }

        mServiceName = (TextView) findViewById( R.id.service_name_edit );
        mServicePrice = (TextView) findViewById( R.id.service_price_edit );
        mServiceNotes = (TextView) findViewById( R.id.service_notes_edit );
        mServicePriceSecCurrency = (TextView) findViewById (R.id.service_price_secondary_edit);

        mServiceDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Service service = new Service();
                service.setServiceName( dataSnapshot.child( extras ).getValue( Service.class ).getServiceName() ); //set the name
                service.setServicePrice( dataSnapshot.child( extras ).getValue( Service.class ).getServicePrice() ); //set the name
                service.setServicePriceSecCurrency( dataSnapshot.child( extras).getValue(Service.class).getServicePriceSecCurrency() );
                service.setServiceNotes( dataSnapshot.child( extras ).getValue( Service.class ).getServiceNotes() );
                service.setServicePlusProfit( dataSnapshot.child( extras ).getValue( Service.class ).getServicePlusProfit() );
                mServiceName.append( service.getServiceName() );
                mServicePrice.append(String.valueOf(  service.getServicePrice() ) );
                mServicePriceSecCurrency.append( String.valueOf(  service.getServicePriceSecCurrency() )  );
                if(service.getServiceNotes() != null) {
                    mServiceNotes.append( service.getServiceNotes() );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        //get the service number from the ServiceFragment
        extras = getIntent().getStringExtra( "serviceNum" );
        if (extras != null) {
            //Show Client Name

        }

        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                    Intent i = new Intent( EditServiceActivity.this, LoginActivity.class );
                    startActivity( i );
                    toastMessage( "Successfully signed out" );
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.edit_menu, menu );
        return true;
    }


    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                updateService();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateService() {
        //get the elements in the dialog
        String serviceName = mServiceName.getText().toString();
        double servicePrice = Double.valueOf(  mServicePrice.getText().toString());
        double serviceSecondaryCurrency = Double.valueOf( mServicePriceSecCurrency.getText().toString() );
        String serviceNotes = mServiceNotes.getText().toString();
        double servicePlusProfit = serviceSecondaryCurrency / secondaryToPrimaryRate;

        //....the rest of infos

        //Check if the user inserted the correct default values
        if ((serviceName.trim().length() > 0 && servicePrice > 0 && serviceSecondaryCurrency > 0)) {
            //set the values at the same clientId selected (update)
            String key = extras;
           /*

            // get user input and set it to result
            // edit text
            Client client = new Client( firstName, lastName );
            mClientDatabaseReference.child( key ).child( "firstName" ).setValue( firstName );
            mClientDatabaseReference.child( key ).child( "lastName" ).setValue( lastName );*/

            Service service = new Service(key, serviceName,servicePrice, serviceSecondaryCurrency);
            mServiceDatabaseReference.child(key).setValue(service);
            mServiceDatabaseReference.child(key).child( "serviceNotes" ).setValue(serviceNotes);
            mServiceDatabaseReference.child( key ).child( "servicePlusProfit").setValue( servicePlusProfit );
            mServiceName.setText("");
            mServicePrice.setText("");
            mServicePriceSecCurrency.setText("");
            mServiceNotes.setText( "" );

            toastMessage("data is up to date.");


            //Go back to client fragment
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("fragmentName","serviceFragment"); //for example
            startActivity(intent);

        } else {
            //else tell the user that there is an error
            toastMessage( "Fill service name and price information!" );
        }



    }
}
