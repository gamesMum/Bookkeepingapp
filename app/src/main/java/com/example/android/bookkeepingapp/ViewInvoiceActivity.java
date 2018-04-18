package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.graphics.Color;
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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewInvoiceActivity extends AppCompatActivity {

    private String TAG = "ViewInvoiceActivity";
    private final int PAID = 1;
    private Toolbar toolbar;
    private TextView mClientName;
    private TextView mIssueDate;
    private TextView mDueDate;
    private TextView mServices;
    private TextView mServicePrice;
    private TextView mNote;
    private TextView mTotal;
    private TextView mInvoiceNum;

    private TextView mNotificationStripe;

    private boolean isOverDue;

    //the client Id and order number stored in invoice
    private String mClientId;
    private ArrayList<String> mOrderNum;
    private ArrayList<String> mServiceNum;
    private Invoice invoice;
    private String extras;

    // Firebase instance variables
    private FirebaseUser user;
    private  String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mInvoiceDatabaseReference;
    private DatabaseReference mClientDatabaseReference;
    private DatabaseReference mOrderDatabaseReference;
    private DatabaseReference mServiceDatabaseReference;

    private String keyInvoice;

    private int mInvoiceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_invoice );
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_invoice_view);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.invoices_text));
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);

        ActionBar actionbar = getSupportActionBar();

        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mInvoiceDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "invoice" );
            mClientDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "client" );
            mOrderDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "order" );
            mServiceDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "service" );
            //For offline sync of data
            mInvoiceDatabaseReference.keepSynced(true);
        }

        //find the view in the xml layout
        mClientName = (TextView) findViewById( R.id.client_invoice_view_text );
        mDueDate = (TextView) findViewById( R.id.due_date_invoice_view_value );
        mIssueDate = (TextView) findViewById( R.id.issue_date_text_invoice_view_value );
        mServices = (TextView) findViewById( R.id.services_invoice_view );
        mServicePrice = (TextView) findViewById( R.id.services_add_price_invoice_view );
        mNote = (TextView) findViewById( R.id.notes_add_invoice_view );
        mInvoiceNum= (TextView) findViewById( R.id.invoice_number_invoice_view_value );
        mTotal = (TextView) findViewById( R.id.invoice_total_value_view );

        mNotificationStripe = (TextView) findViewById( R.id.notification_stripe );

        mOrderNum = new ArrayList<String>(  );
        mServiceNum = new ArrayList<String>(  );

        mInvoiceDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                invoice = new Invoice(  );
                invoice.setDueDate( dataSnapshot.child(extras).getValue(Invoice.class).getDueDate()); //set the name
                invoice.setIssueDate( dataSnapshot.child(extras).getValue(Invoice.class).getIssueDate()); //set the name
                invoice.setInvoiceNumber( dataSnapshot.child(extras).getValue(Invoice.class).getInvoiceNumber());
                invoice.setPaid( dataSnapshot.child(extras).getValue(Invoice.class).getIsPaid() );
                invoice.setOrderNums( dataSnapshot.child(extras).getValue(Invoice.class).getOrderNums() );
                invoice.setClientID( dataSnapshot.child(extras).getValue(Invoice.class).getClientID() );
                invoice.setTotal( dataSnapshot.child(extras).getValue(Invoice.class).getTotal());
                invoice.setPaid( dataSnapshot.child(extras).getValue(Invoice.class).getIsPaid()  );

                keyInvoice = invoice.getInvoiceNumber();
                //check if it is paid or not
                mInvoiceStatus = invoice.getIsPaid();

                //check if the invoice is overdue
                SimpleDateFormat sdf = new SimpleDateFormat( "yyyy / MM / dd " );
                String invoiceDueDate = invoice.getDueDate();
                Date dueDate = null;
                try {
                    dueDate = sdf.parse( invoice.getDueDate() );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date currentDate = null;
                try {
                    currentDate = sdf.parse( getCurrentDate() );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //if it is over due
                if (dueDate.getTime() < currentDate.getTime()) {
                   isOverDue = true;
                }

                //get the clientId and order number for the selected invoice
                //use them to select the right client name and order info (services) and quantity
                //in the future
                mClientId = invoice.getClientID();
                mOrderNum = invoice.getOrderNums();

               // Now I can set the text views with the values of this invoice
                // using invoice.getDueDate()..etc
               mDueDate.setText( invoice.getDueDate() );
               mIssueDate.setText( invoice.getIssueDate() );
               mInvoiceNum.setText( invoice.getInvoiceNumber().
                       substring( invoice.getInvoiceNumber().length() - 5 ) );
               mTotal.setText( String.valueOf( invoice.getTotal()  ) );


                //Check if the invoice is paid or not
                checkIsPaied();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the client name
        mClientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Client client = new Client(  );

                client.setFirstName( dataSnapshot.child(mClientId).getValue(Client.class).getFirstName()); //set the name
                client.setLastName( dataSnapshot.child(mClientId).getValue(Client.class).getLastName()); //set the name
                mClientName.setText( client.getFirstName() + " " + client.getLastName() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the list of services
        mOrderDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(String o : mOrderNum) {
                    Order order = new Order();
                    order.setServiceNum( dataSnapshot.child( o ).getValue( Order.class ).getServiceNum() ); //set the name
                    //get the list of services in each order
                    mServiceNum.add(order.getServiceNum());
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the client name
        mServiceDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(String s : mServiceNum) {
                    Service service = new Service();
                    service.setServiceName( dataSnapshot.child( s ).getValue( Service.class ).getServiceName() ); //set the name
                    service.setServicePrice( dataSnapshot.child( s ).getValue( Service.class ).getServicePrice() );

                    mServices.append( "- " + service.getServiceName() + "\n");
                    mServicePrice.append("₺" + service.getServicePrice()+ "\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //get the selected invoice number from the previous activity
        extras = getIntent().getStringExtra("invoiceNum");
        if (extras != null) {
            //Show Client Name

        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // finish();
                //Go back to Invoice fragment
                Intent intent = new Intent(ViewInvoiceActivity.this,MainActivity.class);
                intent.putExtra("fragmentName","invoiceFragment"); //for example
                startActivity(intent);

            }
        });

        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    // User is signed out
                    //go to login activity
                    Intent i = new Intent(ViewInvoiceActivity.this, LoginActivity.class);
                    startActivity(i);
                    toastMessage("Successfully signed out");
                }
            }
        };
    }

    private void checkIsPaied() {

        //invoice is paid
        if(mInvoiceStatus == 1)
        {
           mNotificationStripe.setText( "PAID" );
           mNotificationStripe.setBackgroundColor( Color.GREEN);
        }
        else if(isOverDue)
        {
            mNotificationStripe.setText( "OVER DUE" );
            mNotificationStripe.setBackgroundColor( Color.RED);
        }
       //still time
        else {
            mNotificationStripe.setText( "NOT PAID" );
            mNotificationStripe.setBackgroundColor( Color.YELLOW);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.invoice_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_delete:
                //your code here
                //Add dialog box
               toastMessage("Oops you just deleted me!");
                return true;
            case R.id.action_edit:
                //View edit activity
                //return the object in the list View
                String invoiceId = extras;
                Intent i = new Intent( ViewInvoiceActivity.this, EditInvoiceActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "invoiceId", invoiceId );
                startActivity( i );
               toastMessage("OK edit me now!");
                return true;
            case R.id.action_pay:
                //mark this invoice as paid
                invoice.setPaid( PAID );
                mInvoiceDatabaseReference.child( keyInvoice ).setValue( invoice );
                checkIsPaied();
                toastMessage("This invoice is Paid");//store the Invoice in the database

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

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat( "yyyy / MM / dd " );
        String strDate = mdformat.format( calendar.getTime() );
        return strDate;
    }
}
