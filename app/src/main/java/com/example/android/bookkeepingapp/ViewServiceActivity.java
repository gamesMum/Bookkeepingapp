package com.example.android.bookkeepingapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class ViewServiceActivity extends AppCompatActivity {


    private String TAG = "ViewServiceActivity";
    private Toolbar toolbar;
    private TextView mServiceName;
    private TextView mServicePrice;
    private TextView mServiceNotes;
    private TextView mServicePlusProfit;
    private  TextView mServicePriceIQ;
    private TextView mServiceProfitRate;
    private String extras;
    private Intent intent;

    // Firebase instance variables
    private FirebaseUser user;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mServiceDatabaseReference;
    public ValueEventListener databaseEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_service );


        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById( R.id.toolbar_service_view );
        setSupportActionBar( toolbar );
        toolbar.setTitle( getString( R.string.Service_view_text ) );
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        //enable back navigation icon for costume toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Service service = new Service();
                service.setServiceName( dataSnapshot.child( extras ).getValue( Service.class ).getServiceName() ); //set the name
                service.setServicePrice( dataSnapshot.child( extras ).getValue( Service.class ).getServicePrice() ); //set the price
                service.setServiceNotes( dataSnapshot.child( extras ).getValue( Service.class ).getServiceNotes() );
                service.setServicePlusProfit( dataSnapshot.child( extras ).getValue( Service.class ).getServicePlusProfit() );
                service.setServicePriceSecCurrency( dataSnapshot.child( extras ).getValue( Service.class ).getServicePriceSecCurrency() );
                //format the price
                //format the price in the label as(2,000,000)
                DecimalFormat formatter = new DecimalFormat( "##,###,###" );
                String servicePriceIQFormatted =  formatter.format( service.getServicePriceSecCurrency() );
                //append the right values for each textView in xml view
                mServiceName.append( " " + service.getServiceName() );
                mServicePrice.append(" " + String.valueOf(  service.getServicePrice() ));
                mServicePlusProfit.append( " " + String.valueOf(  service.getServicePlusProfit() ));
                mServicePriceIQ.append( " " + servicePriceIQFormatted );
                if(service.getServiceNotes() != null) {
                    mServiceNotes.append(" " + service.getServiceNotes() );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } ;
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

        mServiceName = (TextView) findViewById( R.id.service_name_text_view );
        mServicePrice = (TextView) findViewById( R.id.service_price_text_view );
        mServicePlusProfit = (TextView) findViewById( R.id.service_plus_profit );
        mServicePriceIQ = (TextView) findViewById( R.id.service_price_IQ );
        mServiceNotes = (TextView) findViewById( R.id.service_notes_text_view );

        mServiceDatabaseReference.addValueEventListener(databaseEventListener);
        //get the service number from the ServiceFragment
        extras = getIntent().getStringExtra( "serviceNum" );
        if (extras != null) {
            //Show Client Name

        }

      /*  toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               intent = new Intent( view.getContext(), MainActivity.class );
               intent.putExtra( "fragmentName", "serviceFragment" );
               startActivity( intent );
            }
        } );*/



        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    // User is signed out
                    //go to login activity
                    Intent i = new Intent( ViewServiceActivity.this, LoginActivity.class );
                    startActivity( i );
                    toastMessage( "Successfully signed out" );
                }
            }
        };
    }

    //this is the physical back (on the actual phone)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Go back to service fragment
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("fragmentName","serviceFragment"); //for example
        startActivity(intent);
    }

    private void DeleteService(String ServiceNum){

        //delete the service
        mServiceDatabaseReference.child( ServiceNum ).setValue( null );
        //remove the value event listener
        mServiceDatabaseReference.removeEventListener(databaseEventListener );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.view_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Go back to service fragment
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("fragmentName","serviceFragment"); //for example
                startActivity(intent);
                return true;
            case R.id.action_delete:
                //your code here
                //Add dialog box
                if(extras != null) {
                    //delete thi service
                    DeleteService( extras );
                    finish();
                }
                Log.v( TAG, "Oops you just deleted me!" );
                return true;
            case R.id.action_edit:
                //View edit activity
                //return the object in the list View
                String serviceNum = extras;
                Intent i = new Intent( ViewServiceActivity.this, EditServiceActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "serviceNum", serviceNum );
                startActivity( i );
                Log.v( TAG, "OK edit me now!" );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }
}
