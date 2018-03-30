package com.example.android.bookkeepingapp;

import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddInvoiceActivity extends AppCompatActivity {

    private  String TAG = "AddInvoiceActivity";
    //invoice xml elements
    private TextView mClientTextView;
    private TextView mIssueDateTextView;
    private TextView mDueDateTextView;
    private TextView mServicesTextView;
    private TextView mTotalTextView;
    private EditText mNotesEditText;
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
        toolbar = findViewById(R.id.toolbar_invoice);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);


        //Initialize xml element
        mClientTextView = (TextView) findViewById( R.id.client_add );
        mIssueDateTextView = (TextView) findViewById( R.id.issue_date_text_value );
        mDueDateTextView = (TextView) findViewById( R.id.due_date_text_value );
        mServicesTextView = (TextView) findViewById( R.id.services_add );
        mTotalTextView = (TextView) findViewById( R.id.invoice_total_value );
        mNotesEditText = (EditText) findViewById( R.id.notes_add_edit_text );

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

        //set click listeners on textViews (mClientTextView and mServicesTextView)
        mClientTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog with clients
                LayoutInflater li = LayoutInflater.from(AddInvoiceActivity.this);
                View getClientSelectorView = li.inflate(R.layout.client_selector_dialog, null);
                // set dialog message
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddInvoiceActivity.this);

                alertDialogBuilder.setView(getClientSelectorView);

                //get xml element for the client
               // final TextView edUserInput = (TextView) getClientSelectorView.findViewById(R.id.);

                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Select",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                               //get the clientId, go back to new invoice passing the selected lient Id

                            }
                        }).create()
                        .show();
            }
        } );

        mServicesTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog with clients
                Toast.makeText( AddInvoiceActivity.this, "Select Service(s)", Toast.LENGTH_SHORT ).show();

            }
        } );

        mDueDateTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change the due date
                LayoutInflater li = LayoutInflater.from(AddInvoiceActivity.this);
                View getClientSelectorView = li.inflate(R.layout.due_date_selector_dialog, null);
                // set dialog message
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddInvoiceActivity.this);

                alertDialogBuilder.setView(getClientSelectorView);

                //get xml element for the client
                // final TextView edUserInput = (TextView) getClientSelectorView.findViewById(R.id.);

                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Done",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //get the due date , passing the value to new invoice


                            }
                        }).create()
                        .show();

            }
        } );

        //close this activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    /*public void createNewInvoice(String clientID, ArrayList<String> serviceIds) {
        //get the elements in the dialog
       String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        //....the rest of infos

        //Check if the user enterd either the first or the last name
        if (firstName.trim().length() > 0 || lastName.trim().length() > 0) {
            String key = mClientDatabaseReference.push().getKey();
            String company = mCompanyEditText.getText().toString();
           *//*

            // get user input and set it to result
            // edit text
            Client client = new Client( firstName, lastName );
            mClientDatabaseReference.child( key ).child( "firstName" ).setValue( firstName );
            mClientDatabaseReference.child( key ).child( "lastName" ).setValue( lastName );*//*

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
*/
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
