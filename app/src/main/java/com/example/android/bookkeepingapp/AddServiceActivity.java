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

public class AddServiceActivity extends AppCompatActivity {

    private  String TAG = "AddServiceActivity";
    private EditText mServiceName;
    private EditText mOriginalPrice;
    private EditText mPlusProfitSec;
    private EditText mNotes;
    private Toolbar toolbar;
    Intent intent;
    //I need to get this via service
    private static double secondaryToPrimaryRate = 203.518;


    private String clientID;

    //Firebase variables
    private  FirebaseUser user;
    private  String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mServiceDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_service );



        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_service);
        setSupportActionBar(toolbar);
        //add (X) icon to the custom toolbar
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        intent = new Intent(this,MainActivity.class);

        //Initialize xml element
        mServiceName = (EditText) findViewById( R.id.service_name );
        mOriginalPrice = (EditText) findViewById( R.id.service_price );
        mPlusProfitSec = (EditText) findViewById( R.id.service_plus_profit );
        mNotes = (EditText) findViewById( R.id.service_notes );

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
        }
        //store the data under loggedin user Id
        mServiceDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "service" );

        //close this activity when we press (X) icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Go back to service fragment
            intent.putExtra("fragmentName","serviceFragment");
            startActivity(intent);
            }
        });




    }

    public void createNewService() {
        //get the elements in the dialog
        String serviceName = mServiceName.getText().toString();
        double servicePrice =  Double.parseDouble(mOriginalPrice.getText().toString());
        double servicePricePlusProfit = Double.parseDouble( mPlusProfitSec.getText().toString() );
        String serviceNotes = mNotes.getText().toString();

       //calculate the final price in Iraqi dinar (for now)
       double servicePriceSecCurrency = servicePricePlusProfit;
       double servicePlusProfit = servicePriceSecCurrency / secondaryToPrimaryRate;
        //Check if the user enterd the service name and price
        if (serviceName.trim().length() > 0 && servicePrice > 0) {
            String key = mServiceDatabaseReference.push().getKey();

            Service service = new Service(key, serviceName,servicePrice, servicePriceSecCurrency);
            //set the necessary inforatio and store in database
            mServiceDatabaseReference.child(key).setValue(service);
            //set the notes if any
            mServiceDatabaseReference.child(key).child( "serviceNotes" ).setValue(serviceNotes);
            //set the service price plus profit & price in IQ we calculated earlier
            mServiceDatabaseReference.child( key ).child( "servicePlusProfit").setValue( servicePlusProfit );

            toastMessage("New Service has been saved.");
            mServiceName.setText("");
            mOriginalPrice.setText("");
            mNotes.setText( "" );
            mPlusProfitSec.setText("");

            //Go back to service fragment
           /* Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("fragmentName","serviceFragment");
            startActivity(intent);*/

        } else {
            //else tell the user that there is an error
            toastMessage( "did you forget to put service name or price" );
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
                createNewService();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
