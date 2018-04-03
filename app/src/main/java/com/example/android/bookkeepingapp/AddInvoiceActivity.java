package com.example.android.bookkeepingapp;

import android.app.Dialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddInvoiceActivity extends AppCompatActivity {

    private String TAG = "AddInvoiceActivity";
    //invoice xml elements
    private TextView mClientTextView;
    private TextView mIssueDateTextView;
    private TextView mDueDateTextView;
    private TextView mServicesTextView;
    private TextView mTotalTextView;
    private EditText mNotesEditText;
    private Toolbar toolbar;
    private ListView mClientListView;
    private ListView mServiceListView;
    private ProgressBar mProgressBar;



    private String clientID;

    //Firebase variables
    private FirebaseUser user;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mInvoiceDatabaseReference;
    private DatabaseReference mClientDatabaseReference;
    private DatabaseReference mServiceDatabaseReference;
    private ClientAdapter mClientAdapter;
    private ServiceAdapterCheckBox mServiceAdapter;
    ProgressBar mClientDialogProgressBar;
    ProgressBar mServiceDialogProgressBar;
    String clientIdFromDialog;
    //**************************************************
    private ChildEventListener mChiltEventListener;
    private ChildEventListener mServiceEventListener;
    //**************************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_invoice );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById( R.id.toolbar_invoice );
        setSupportActionBar( toolbar );
        toolbar.setNavigationIcon( R.drawable.ic_close_black_24dp );


        //Initialize xml element
        mClientTextView = (TextView) findViewById( R.id.client_add );
        mIssueDateTextView = (TextView) findViewById( R.id.issue_date_text_value );
        mDueDateTextView = (TextView) findViewById( R.id.due_date_text_value );
        mServicesTextView = (TextView) findViewById( R.id.services_add );
        mTotalTextView = (TextView) findViewById( R.id.invoice_total_value );
        mNotesEditText = (EditText) findViewById( R.id.notes_add_edit_text );
        mClientDialogProgressBar = null;

        //set the values for the issue date and due date textviews
        //Due date default is same date of issue
        mIssueDateTextView.setText( getCurrentDate() );
        mDueDateTextView.setText( getCurrentDate());

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        //get the data for stored invoices for the logged in user
        mInvoiceDatabaseReference = mFirebaseDatabase.getReference().
                child( userID ).child( "invoice" );
        //get the data for stored clients for the logged in user
        mClientDatabaseReference = mFirebaseDatabase.getReference().
                child( userID ).child( "client" );
        //get the data for stored services for the logged in user
        mServiceDatabaseReference = mFirebaseDatabase.getReference().
                child( userID ).child( "service" );


        //set click listeners on textViews (mClientTextView and mServicesTextView)
        mClientTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show list of stored clients
                createClientDialog();

            }
        } );

        //when click on services open a dialog with the available services
        mServicesTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog with services
                createServiceDialog();

            }
        } );

        //change the due date for the invoice from default
        mDueDateTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change the due date
                LayoutInflater li = LayoutInflater.from( AddInvoiceActivity.this );

                //inflate the dialog view and get the RadioGroup element in it
                final View getDateSelectorView = li.inflate( R.layout.due_date_selector_dialog, null );
                final RadioGroup daysAfterRadioGroup = getDateSelectorView.findViewById( R.id.days_radio_group );
                // set dialog message
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( AddInvoiceActivity.this );

                alertDialogBuilder.setView( getDateSelectorView );

                //When Done is pressed do the following
                alertDialogBuilder.setCancelable( true ).setPositiveButton( "Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //get the days selected , passing the value to new invoice
                        int selectedId = daysAfterRadioGroup.getCheckedRadioButtonId();
                        RadioButton checkedRadio = getDateSelectorView.findViewById( selectedId );
                        String checkedRadioText = checkedRadio.getText().toString();
                        //set due date according to the selected days number
                      switch (checkedRadioText)
                       {
                           case "On date of issue":
                               mDueDateTextView.setText( setDueDateAfter( 0 ) );
                               break;
                           case "After 15 days":
                              mDueDateTextView.setText( setDueDateAfter( 15 ) );
                              break;
                           case "After 30 days":
                               mDueDateTextView.setText( setDueDateAfter( 30 ) );
                               break;
                           case "After 45 days":
                               mDueDateTextView.setText( setDueDateAfter( 45 ) );
                               break;
                           case "After 60 days":
                               mDueDateTextView.setText( setDueDateAfter( 60 ) );
                               break;


                       }

                    }
                } ).create().show();


            }
        } );

        //close this activity
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );

    }

    //helping method to create, populate and showing the client dialog box
    private void createClientDialog() {

        //initializing the dialog box with the correct layout
        final Dialog dialog = new Dialog( AddInvoiceActivity.this );
        dialog.setContentView( R.layout.client_selector_dialog );

        dialog.setTitle( "Clients" );
        dialog.setCancelable( true );
        dialog.setCanceledOnTouchOutside( true );

        //setting dialog listeners
        dialog.setOnCancelListener( new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                toastMessage( "OnCancelListener" );
                //use client id from dialog when the dialog is cloased
                if(clientIdFromDialog != null) {
                    mClientDatabaseReference.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Client client = new Client();
                            client.setFirstName( dataSnapshot.child( clientIdFromDialog ).getValue( Client.class ).getFirstName() ); //set the name
                            client.setLastName( dataSnapshot.child( clientIdFromDialog ).getValue( Client.class ).getLastName() ); //set the name
                            mClientTextView.setText( client.getFirstName() + " " + client.getLastName() );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    } );
                }

                //detach database reader on canceling
                detachDatabaseReadListener();
            }
        } );

        dialog.setOnDismissListener( new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                toastMessage( "OnDismissListener" );
                //detach database reader on dismissing
                detachDatabaseReadListener();
            }
        } );

        //Prepare ListView in dialog
        mClientListView = (ListView) dialog.findViewById( R.id.client_list_view_selector );
        mClientDialogProgressBar = (ProgressBar) dialog.findViewById( R.id.progressBar_client_selector );
        mClientDialogProgressBar.setVisibility( View.VISIBLE );
        // Initialize client ListView and its adapter
        final List<Client> clients = new ArrayList<>();
        mClientAdapter = new ClientAdapter( AddInvoiceActivity.this, R.layout.client_item, clients );
        mClientListView.setAdapter( mClientAdapter );
        //attach the data base reader listener to display the latest clients records
        attachDatabaseReadListener();

        //set on item selected listener
        //on clock go back to this activity with the client Id
        mClientListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //return the object in the list View
                Client client = clients.get( position );
               clientIdFromDialog = client.getClientId();
                Log.v(TAG, client.toString());
               dialog.cancel();

            }
        } );
    dialog.show();//display the dialog

    }

    //helping method to create, populate and showing the client dialog box
    private void createServiceDialog() {

        //initializing the dialog box with the correct layout
        Dialog dialog = new Dialog( AddInvoiceActivity.this );
        dialog.setContentView( R.layout.services_selector_dialog );

        dialog.setTitle( "Services" );
        dialog.setCancelable( true );
        dialog.setCanceledOnTouchOutside( true );

        //setting dialog listeners
        dialog.setOnCancelListener( new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                toastMessage( "OnCancelListener" );
                //detach database reader on canceling
                detachServiceDatabaseReadListener();
            }
        } );

        dialog.setOnDismissListener( new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                toastMessage( "OnDismissListener" );
                //detach database reader on dismissing
                detachServiceDatabaseReadListener();
            }
        } );

        //Prepare ListView in dialog
        mServiceListView = (ListView) dialog.findViewById( R.id.service_list_view_selector );
        mServiceDialogProgressBar = (ProgressBar) dialog.findViewById( R.id.progressBar_service_selector );
        mServiceDialogProgressBar.setVisibility( View.VISIBLE );
        // Initialize client ListView and its adapter
        final List<Service> services = new ArrayList<>();
        mServiceAdapter = new ServiceAdapterCheckBox( AddInvoiceActivity.this, R.layout.service_item_check_box, services );
        mServiceListView.setAdapter( mServiceAdapter );
        //attach the data base reader listener to display the latest clients records
        attachServiceDatabaseReadListener();

        dialog.show();//display the dialog

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
    private void detachServiceDatabaseReadListener() {
        if (mServiceEventListener != null) {

            mServiceDatabaseReference.removeEventListener( mServiceEventListener );
            mServiceEventListener = null;
        }
    }

    private void detachDatabaseReadListener() {
        if (mChiltEventListener != null) {

            mClientDatabaseReference.removeEventListener( mChiltEventListener );
            mChiltEventListener = null;
        }
    }

    private void attachServiceDatabaseReadListener() {
        if (mServiceEventListener == null) {
            mServiceEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Service service = dataSnapshot.getValue( Service.class );
                    mServiceAdapter.add( service );
                    if(mServiceAdapter.getCount() > 0)
                    {
                        mServiceDialogProgressBar.setVisibility( View.INVISIBLE );
                    }

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mServiceDatabaseReference.addChildEventListener( mServiceEventListener );
        }


    }

    private void attachDatabaseReadListener() {
        if (mChiltEventListener == null) {
            mChiltEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Client client = dataSnapshot.getValue( Client.class );
                    mClientAdapter.add( client );
                    if(mClientAdapter.getCount() > 0)
                    {
                        mClientDialogProgressBar.setVisibility( View.INVISIBLE );
                    }

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mClientDatabaseReference.addChildEventListener( mChiltEventListener );
        }


    }

    //get the date after the desired number of days
    //this is the Due date for the invoice
    public String setDueDateAfter( int days)
    {
        String date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(mIssueDateTextView.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //add the number of days for the invoice over due
        c.add(Calendar.DAY_OF_MONTH, days);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy / MM / dd ");
        //get the date in the correct format
        date = sdf1.format(c.getTime());

        return date;
    }

    private String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
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
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.edit_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //createNewInvoice("", {"",""});
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }
}
