package com.example.android.bookkeepingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class AddInvoiceActivity extends AppCompatActivity {

    private String TAG = "AddInvoiceActivity";
    //invoice xml elements
    private TextView mClientTextView;
    private TextView mIssueDateTextView;
    private TextView mDueDateTextView;
    private TextView mServicesTextView;
    private TextView mSubTotalTextView;
    private TextView mTotalTextView;
    private TextView mTotalExpensesTextView;
    private TextView mProfitTextView;
    private EditText mNotesEditText;
    private EditText mShipmentCost;
    private String ServiceQuantity;
    private Toolbar toolbar;
    private ListView mClientListView;
    private ListView mServiceListView;
    private TextView mServicePriceTextView;
    private TextView mInvoiceNumberTextView;
    private ProgressBar mProgressBar;

    private CheckedTextView ctv;
    private TextView mServiceQuantity;
    private Button mMinusBtn;
    private Button mPlusBtn ;

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
    private DatabaseReference mOrderDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private ClientAdapter mClientAdapter;
    private ServiceAdapterCheckBox mServiceAdapter;
    ProgressBar mClientDialogProgressBar;
    ProgressBar mServiceDialogProgressBar;
    private String clientIdFromDialog;
    private Intent intent;
    //store the selected services and the quantity of each one
    private Hashtable<String, Integer> selectedServicesHash;
    //List of orders for each service
    private ArrayList<Order> orderArrayList;
    //the invoice object that will contain the list of orders
    private Invoice invoice;
    private   String keyInvoice;
    private double totalPrice;
    private double invoiceExpenses;
    private double invoicePlusProfitFirstCurrence;
    private double currentTotalExp;
    private double userNewExp;
    private double invoiceProfit;
    private User userData;
    //**************************************************
    private ChildEventListener mChiltEventListener;
    private ChildEventListener mServiceEventListener;
    //**************************************************
    //create array of selectes textviews
    private  ArrayList<TextView> selectedTextViews;
    private ArrayList<String> selectedServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_invoice );
        //Create new Invoice instence with giving the first Id
        mInvoiceNumberTextView = (TextView) findViewById( R.id.invoice_number_text_value );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById( R.id.toolbar_invoice );
        setSupportActionBar( toolbar );
        toolbar.setNavigationIcon( R.drawable.ic_close_black_24dp );
        intent = new Intent(this,MainActivity.class);
        userData = new User();

        //Initialize xml element
        mClientTextView = (TextView) findViewById( R.id.client_add );
        mIssueDateTextView = (TextView) findViewById( R.id.issue_date_text_value );
        mDueDateTextView = (TextView) findViewById( R.id.due_date_text_value );
        mServicesTextView = (TextView) findViewById( R.id.services_add );
        mServicePriceTextView = (TextView) findViewById( R.id.services_add_price );
        mSubTotalTextView = (TextView) findViewById( R.id.invoice_sub_total_value );
        mTotalTextView = (TextView) findViewById( R.id.invoice_total_value ) ;
        mShipmentCost = (EditText) findViewById( R.id.invoice_shipment_value_view_add );
        mTotalExpensesTextView = (TextView) findViewById( R.id.invoice_expenses_value );
        mProfitTextView = (TextView) findViewById( R.id.invoice_profit_value );
        mNotesEditText = (EditText) findViewById( R.id.notes_add_edit_text );
        mClientDialogProgressBar = null;

        selectedServicesHash = new Hashtable<String, Integer>();
        selectedTextViews = new ArrayList<>(  );
        selectedServices = new ArrayList<>(  );

        clientIdFromDialog = "";
        orderArrayList = new ArrayList<Order>( );

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
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "user" );
        }
        //get the data for stored invoices for the logged in user
        mInvoiceDatabaseReference = mFirebaseDatabase.getReference().
                child( userID ).child( "invoice" );
        //get the data for stored invoices for the logged in user
        mOrderDatabaseReference = mFirebaseDatabase.getReference().
                child( userID ).child( "order" );
        //get the data for stored clients for the logged in user
        mClientDatabaseReference = mFirebaseDatabase.getReference().
                child( userID ).child( "client" );
        //get the data for stored services for the logged in user
        mServiceDatabaseReference = mFirebaseDatabase.getReference().
                child( userID ).child( "service" );

        //get new key for the invoice to display it in the text view
        keyInvoice = mInvoiceDatabaseReference.push().getKey();
        //set the text to that number (or part of it)
        mInvoiceNumberTextView.setText( keyInvoice.substring( keyInvoice.length() - 5 ) );
        //set click listeners on textViews (mClientTextView and mServicesTextView)
        mClientTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear privious texts__no repetition problem
                //-------------------------------------------
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
                //create, and set the due date according to the number picked
                createDaysDialog();
            }
        } );

        //close this activity
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Go back to invoice fragment
                intent.putExtra("fragment Name","invoiceFragment"); //for example
                startActivity(intent);
            }
        } );

        //get the current User expenses (before invoice is created)
        mUserDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the current total expenses for the user
                userData.setTotalExpenses( dataSnapshot.getValue( User.class ).getTotalExpenses() ); //set the ecp
                //add the sum of this invoice expenses to the current user's
                //expenses
                //calculate the total expenses
                // double newExpenses  = invoiceExpenses + userData.getTotalExpenses();
                Log.v(TAG, "The value of the  expenses before invoice: " + userData.getTotalExpenses());
                currentTotalExp = userData.getTotalExpenses();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


    }

    //Show the days selector dialog and set the due date after the selected date
    private void createDaysDialog()
    {
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
                //toastMessage( "OnCancelListener" );
                //use client id from dialog when the dialog is cloased
                if(clientIdFromDialog.length() != 0) {
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
                }else
                    mClientTextView.setText( R.string.add_client_text );

                //detach database reader on canceling
                detachDatabaseReadListener();
            }
        } );

        dialog.setOnDismissListener( new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                //toastMessage( "OnDismissListener" );
                //detach database reader on dismissing


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
        //change the due date
        LayoutInflater li = LayoutInflater.from( AddInvoiceActivity.this );

        //inflate the dialog view and get the RadioGroup element in it
        final View getServiceSelectorView = li.inflate( R.layout.services_selector_dialog, null );


        // set dialog message
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( AddInvoiceActivity.this );

        //find the editText in the dialog
        EditText serviceSearchBox = (EditText) getServiceSelectorView.findViewById( R.id.add_service_search_txt_dialog );

        if(serviceSearchBox != null) {        //attach the filter to it
            serviceSearchBox.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                    // Call back the Adapter with current character to Filter
                    mServiceAdapter.getFilter().filter( s.toString() );

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            } );
        }else toastMessage( "the search text is not found!!" );

        alertDialogBuilder.setView( getServiceSelectorView );
        alertDialogBuilder.setTitle( "Services" );
        alertDialogBuilder.setCancelable( true );

        //setting dialog listeners
        alertDialogBuilder.setOnCancelListener( new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                //  toastMessage( "OnCancelListener" );
                //detach database reader on canceling
                detachServiceDatabaseReadListener();
            }
        } );

        alertDialogBuilder.setOnDismissListener( new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // toastMessage( "OnDismissListener" );
                //detach database reader on dismissing
                detachServiceDatabaseReadListener();
            }
        } );

        //Prepare ListView in dialog
        mServiceListView = (ListView) getServiceSelectorView.findViewById( R.id.service_list_view_selector );
        mServiceDialogProgressBar = (ProgressBar) getServiceSelectorView.findViewById( R.id.progressBar_service_selector );
        mServiceDialogProgressBar.setVisibility( View.VISIBLE );
        // Initialize client ListView and its adapter
        final List<Service> services = new ArrayList<>();
        mServiceAdapter = new ServiceAdapterCheckBox( AddInvoiceActivity.this,
                R.layout.service_item_check_box, services, selectedServicesHash);
        mServiceListView.setAdapter( mServiceAdapter );
        //mServiceListView.setItemsCanFocus( false );
        // we want multiple clicks
        mServiceListView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
        //mServiceListView.setItemsCanFocus(false);
        //attach the data base reader listener to display the services
        attachServiceDatabaseReadListener();


        mServiceListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //default quantity for services
                int defaultQuantity = 1;
                //find the checkedTextView inside the relative view
                ctv = (CheckedTextView) view.findViewById( R.id.checkedTextView );
                mServiceQuantity = (TextView) view.findViewById( R.id.quantity_tv );
                mMinusBtn = ( Button) view.findViewById( R.id.minus_btn );
                mPlusBtn = (Button) view.findViewById( R.id.plus_btn );
                ((CheckedTextView) ctv).toggle();
                Service service = (Service) parent.getItemAtPosition(position);
                if (ctv.isChecked()) {
                    selectedServicesHash.put( service.getServiceNum(), defaultQuantity);//for default
                    selectedServices.add(service.getServiceNum());
                    //get the textview at selected position
                    //and store it in the list array
                    selectedTextViews.add((TextView) parent.getChildAt( position -  parent.getFirstVisiblePosition())
                            .findViewById( R.id.quantity_tv ));
                    //check if the position value is null
                    //for debug
                   /* if (parent.getChildAt(position-parent.getFirstVisiblePosition()) != null){
                        for(TextView tv : selectedTextViews) {
                            Log.v( TAG, "the value of selected textviews:" + tv.getText() );
                        }
                    }
                    else
                    {
                        Log.v(TAG, "we can't get to that text view!");
                    }*/


                    //get the value of the quantity check box
                    //ServiceQuantity = mServiceQuantity.getText().toString();
                    //make the quantity option visible
                    mMinusBtn.setVisibility( View.VISIBLE );
                    mPlusBtn.setVisibility( View.VISIBLE );
                    mServiceQuantity.setVisibility( View.VISIBLE );

                } else {
                    //remove the service and quantity from table
                    selectedServicesHash.remove( service.getServiceNum() );
                    selectedServices.remove( service.getServiceNum() );
                    //else remove the textview at that position
                    //if it is nor not selected
                    selectedTextViews.remove( (TextView) parent.getChildAt( position - parent.getFirstVisiblePosition() )
                            .findViewById( R.id.quantity_tv ) );
                    //the quantity option will be invisible
                    mMinusBtn.setVisibility( View.INVISIBLE );
                    mPlusBtn.setVisibility( View.INVISIBLE );
                    mServiceQuantity.setVisibility( View.INVISIBLE );

                }

            }

        } );

        alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //if there is service(s) selected
                if(!selectedServicesHash.isEmpty()) {
                    for(TextView tv : selectedTextViews) {
                        Log.v( TAG, "the value of selected textviews:" + tv.getText() );
                    }

                    //remove the initial text
                    mServicesTextView.setText( " " );
                    mServicePriceTextView.setText( " " );
                    mServiceDatabaseReference.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            totalPrice = 0;
                            invoiceExpenses = 0;
                            invoiceProfit = 0;
                            invoicePlusProfitFirstCurrence=0;
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            //put all the services num of the hashtable in a list
                            Set<String> serviceNums= selectedServicesHash.keySet();
                            //for every service selected by the user, display the name
                            for(String s : serviceNums) {
                                Service service = new Service();
                                service.setServiceName( dataSnapshot.child( s ).getValue( Service.class ).getServiceName() ); //set the name
                                service.setServicePrice( dataSnapshot.child( s ).getValue(Service.class).getServicePrice() );
                                service.setServicePriceSecCurrency( dataSnapshot.child( s ).getValue(Service.class).getServicePriceSecCurrency() );
                                service.setServicePlusProfit( dataSnapshot.child( s ).getValue(Service.class).getServicePlusProfit() );
                                //format the  price ID
                                //format the price in the label as(2,000,000)
                                String priceIDFormatted = formatPrice( service.getServicePriceSecCurrency() * selectedServicesHash.get(s) );
                                //take the sum of all the services and produce the total
                                //for customers
                                //multiply by the quantity of each service (1 or more)
                                totalPrice += service.getServicePriceSecCurrency()*selectedServicesHash.get(s);
                                //total expenses (original price in tr) for user
                                invoiceExpenses += service.getServicePrice()*selectedServicesHash.get(s);

                                invoicePlusProfitFirstCurrence += service.getServicePlusProfit()
                                        *selectedServicesHash.get(s);
                                Log.v(TAG, "invoicePlusProfitFirstCurrence values" +  invoicePlusProfitFirstCurrence);
                                mServicesTextView.append( "- " + service.getServiceName() +
                                        "X " +selectedServicesHash.get(s) + "\n");
                                mServicePriceTextView.append(priceIDFormatted  + "\n");


                            }
                            //calculate the net profit
                            //net profit (for user)
                            invoiceProfit += (invoicePlusProfitFirstCurrence-invoiceExpenses);
                            //format the total price
                            //format the price in the label as(2,000,000)
                            String totalPriceFormatted = formatPrice( totalPrice );

                            //set the text View with the total value for each field
                            mSubTotalTextView.setText( totalPriceFormatted );
                            if(mShipmentCost.getText() != null || Integer.valueOf( mShipmentCost.getText().toString() )!=0) {
                                int shipmentCost = Integer.valueOf( mShipmentCost.getText().toString() );
                                mTotalTextView.setText(totalPriceFormatted + shipmentCost );
                            }
                            else
                                mTotalTextView.setText(totalPriceFormatted  );
                            mTotalExpensesTextView.setText( String.valueOf( invoiceExpenses ) );
                            mProfitTextView.setText( String.valueOf( invoiceProfit ) );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    } );
                }
                else
                {
                    //if there is no service selected reset text views
                    mServicesTextView.setText( R.string.service_add );
                    mServicePriceTextView.setText("");
                    mTotalTextView.setText( "ID 0" );
                    mTotalExpensesTextView.setText( "/" );
                    mProfitTextView.setText( "/" );
                    totalPrice = 0;
                }

            }

        });

        alertDialogBuilder.show();//display the dialog

    }





    private void detachServiceDatabaseReadListener() {
        if (mServiceEventListener != null) {

            mServiceDatabaseReference.removeEventListener( mServiceEventListener );
            mServiceEventListener = null;
        }
    }

    /*private void detacheUserDataReadListener(){
        if(mUser)
    }*/

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
                    if(mServiceAdapter.getCount() > 0 || mServiceAdapter.getCount() == 0)
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

    //create list of the orders (for each service, quantity, and price)
    //create new Invoice contains the list of orders
    public void createNewInvoice()
    {
        //Check if the client and services are selected (not empty)
        if(clientIdFromDialog.length() != 0 && selectedServicesHash.size() != 0)
        {
            //Create new Order object passing the information provided by the user
            //create order for every service and add it to the ordet list
            Set<String> servicesKeys = selectedServicesHash.keySet();
            for(String s: servicesKeys)
            {
                //generate new key for each order
                String keyOrder = mOrderDatabaseReference.push().getKey();

                //set the order value with the client Id and service
                // (selectedServicesHash.get(s) represent the quantity for eac service)
                Order order = new Order(keyOrder,clientIdFromDialog, s, selectedServicesHash.get(s) );
                orderArrayList.add(order);//use this to get total and create invoice
                //fetch order numbers from arrayList
                ArrayList<String> orderNums = new ArrayList<String>(  );
                for(Order o : orderArrayList)
                {
                    orderNums.add(o.getOrderNum());
                }
                //Create the new Invoice
                invoice = new Invoice(keyInvoice, clientIdFromDialog, orderNums,
                        mIssueDateTextView.getText().toString(), mDueDateTextView.getText().toString(), totalPrice,
                        invoiceProfit, invoiceExpenses, Integer.valueOf(mShipmentCost.getText().toString() ));
                //store order in database when invoice is saved and created
                mOrderDatabaseReference.child(keyOrder).setValue(order);
                //store the Invoice in the database
                mInvoiceDatabaseReference.child( keyInvoice ).setValue( invoice );
                if (mNotesEditText.getText().length() != 0)
                {
                    mInvoiceDatabaseReference.child( keyInvoice ).
                            child( "invoiceNote" ).setValue( mNotesEditText.getText().toString()  );
                }
                //calculate the user total expenses (rounded)
                userNewExp =  currentTotalExp + invoiceExpenses;
                toastMessage( "New Invoice is created" );
                //Update the user expenses by updating the currentTotalExp to the userNewExp
                mUserDatabaseReference.child( "totalExpenses" ).setValue( userNewExp );
                Log.v(TAG, "The current value of the  expenses :" + userData.getTotalExpenses());


                //Go back to Invoice fragment
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("fragmentName","invoiceFragment"); //for example
                startActivity(intent);

            }
        }
        else
            toastMessage( "Please pick a client and service!" );
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
                createNewInvoice();
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private String formatPrice(double price){
        //format the price in the label as(2,000,000)
        DecimalFormat formatter = new DecimalFormat( "##,###,###" );
        String priceFormatted = formatter.format( price );
        return priceFormatted;
    }


    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }
}