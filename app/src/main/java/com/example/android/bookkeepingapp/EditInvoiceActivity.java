package com.example.android.bookkeepingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
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

public class EditInvoiceActivity extends AppCompatActivity {

    private  String TAG = "EditInvoiceActivity";
    private TextView eIClientNameTxt;
    private TextView eIDueDateTxt;
    private TextView eIServisesTxt;
    private TextView eIServicesPriceTxt;
    private TextView eIIssueDate;
    private TextView eINoteTxt;
    private TextView eIInvoiceNum;
    private TextView eIExpensesTxt;
    private TextView eIProfitTxt;
    private TextView eITotal;
    private TextView eISubTotal;
    private EditText eIShipmentCost;
    //initialized only once
    private static double  invoiceExpensesBeforeUpdate;


    //store the selected services and the quantity of each one
    private Hashtable<String, Integer> selectedServicesHash;
    private ArrayList<String> selectedServices;
    private ArrayList<TextView> selectedTextViews;
    //List of orders for each service
    private ArrayList<Order> orderArrayList;

    private Toolbar toolbar;
    private ListView eIServiceListView;
    private ListView eIClientListView;
    private ClientAdapter eIClientAdapter;
    private ServiceAdapterCheckBox mServiceAdapter;
    ProgressBar mClientDialogProgressBar;
    ProgressBar mServiceDialogProgressBar;
    private String clientIdFromDialog;
    private String extras;

    private CheckedTextView ctv;
    private TextView mServiceQuantity;
    private Button mMinusBtn;
    private Button mPlusBtn ;

    //List of orders for each service
    private static ArrayList<String> orderNumsBeforeUpdate;
    private String clientId;

    //the invoice object that will contain the list of orders
    private Invoice invoice;
    private double totalPrice;
    private int shipmentCost;
    private double invoiceExpenses;
    private double invoicePlusProfitFirstCurrence;
    private double currentTotalExp;
    private double userNewExp;
    private double invoiceProfit;
    private User userData;

    //Firebase variables
    private FirebaseUser user;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mInvoiceDatabaseReference;
    private DatabaseReference mClientDatabaseReference;
    private DatabaseReference mOrderDatabaseReference;
    private DatabaseReference mServiceDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mServiceEventListener;
    private ChildEventListener mChiltEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_invoice );
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.edit_toolbar_invoice);
        setSupportActionBar(toolbar);
        userData = new User();
        toolbar.setTitle(getString(R.string.invoice_edit));
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        eIClientNameTxt = (TextView) findViewById( R.id.client_edit );
        eIDueDateTxt = (TextView) findViewById( R.id.edit_due_date_text_value );
        eIServisesTxt = (TextView) findViewById( R.id.services_edit );
        eIServicesPriceTxt = (TextView) findViewById( R.id.edit_services_add_price );
        eINoteTxt = (TextView) findViewById( R.id.notes_edit_text );
        eIIssueDate = (TextView) findViewById( R.id.edit_issue_date_text_value );
        eIInvoiceNum = (TextView)findViewById( R.id.edit_invoice_number_text_value );
        eIExpensesTxt = (TextView) findViewById( R.id.edit_invoice_expenses_value ) ;
        eIProfitTxt = (TextView) findViewById( R.id.edit_invoice_profit_value );
        eITotal = (TextView) findViewById( R.id.edit_invoice_total_value);
        eISubTotal = (TextView) findViewById( R.id.edit_invoice_sub_total_value_text) ;
        eIShipmentCost = (EditText) findViewById( R.id.edit_invoice_shipment_text_value );


        mClientDialogProgressBar = null;

        //populate the hash table with the already selected services
        selectedServicesHash = new Hashtable<String, Integer>();
        selectedServices = new ArrayList<>(  );
        selectedTextViews = new ArrayList<>(  );

        clientIdFromDialog = "";
        orderArrayList = new ArrayList<Order>( );
        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            mInvoiceDatabaseReference = mFirebaseDatabase.getReference().child( userID ).child( "invoice" );
            mClientDatabaseReference =  mFirebaseDatabase.getReference().child( userID ).child( "client" );
            mOrderDatabaseReference = mFirebaseDatabase.getReference().child( userID).child( "order" );
            mServiceDatabaseReference = mFirebaseDatabase.getReference().child( userID ).child( "service" );
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "user" );
        }

        //take the invoice Id selected from the previous activity
        extras = getIntent().getStringExtra("invoiceId");
        if (extras != null) {
            //Show invoice number

        }

        eIServisesTxt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog with services
                createServiceDialog();
            }
        } );

        //set click listeners on textViews (mClientTextView and mServicesTextView)
        eIClientNameTxt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear privious texts__no repetition problem
                //-------------------------------------------
                //show list of stored clients
                createClientDialog();

            }
        } );
        //change the due date for the invoice from default
        eIDueDateTxt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create, and set the due date according to the number picked
                createDaysDialog();
            }
        } );

        mInvoiceDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Invoice invoice = new Invoice(  );
                invoice.setClientID( dataSnapshot.child(extras).getValue(Invoice.class).getClientID());
                invoice.setIssueDate(dataSnapshot.child(extras).getValue(Invoice.class).getIssueDate());
                invoice.setDueDate( dataSnapshot.child(extras).getValue(Invoice.class).getDueDate());
                invoice.setInvoiceExpenses( dataSnapshot.child(extras).getValue(Invoice.class).getInvoiceExpenses());
                invoice.setInvoiceProfit( dataSnapshot.child(extras).getValue(Invoice.class).getInvoiceProfit());
                invoice.setOrderNums( dataSnapshot.child(extras).getValue(Invoice.class).getOrderNums());
                invoice.setPaid( dataSnapshot.child(extras).getValue(Invoice.class).getPaid());
                invoice.setTotal( dataSnapshot.child(extras).getValue(Invoice.class).getTotal());
                invoice.setInvoiceNumber(  dataSnapshot.child(extras).getValue(Invoice.class).getInvoiceNumber() );
                invoice.setInvoiceNote(  dataSnapshot.child(extras).getValue(Invoice.class).getInvoiceNote() );
                invoice.setShippingCost(  dataSnapshot.child(extras).getValue(Invoice.class).getShippingCost() );

                //if the invoice has some notes
                if(invoice.getInvoiceNote() != null)
                {
                    eINoteTxt.setText( invoice.getInvoiceNote() );
                }

                eIIssueDate.setText( invoice.getIssueDate() );
                eIDueDateTxt.setText( invoice.getDueDate() );
                eIExpensesTxt.setText( String.valueOf( invoice.getInvoiceExpenses() ) );
                //store the old value in case it was updated
                invoiceExpensesBeforeUpdate = invoice.getInvoiceExpenses();
                eIProfitTxt.setText( String.valueOf( invoice.getInvoiceProfit() ) );
                eISubTotal.setText( String.valueOf( invoice.getTotal() ) );//this is the total without the shipment
                eIShipmentCost.setText( String.valueOf( invoice.getShippingCost() ) );
                double invoiceTotalPlusShipment = invoice.getTotal() + invoice.getShippingCost();
                eITotal.setText( String.valueOf( invoiceTotalPlusShipment ) );
                eIInvoiceNum.setText( invoice.getInvoiceNumber().
                        substring( invoice.getInvoiceNumber().length() - 5 ) );
                //get the client id to get the name
                clientId = invoice.getClientID();
                //get the orders to get the services and quantities
                orderNumsBeforeUpdate = invoice.getOrderNums();


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

        //get the current User expenses (before invoice is updated)
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

        

        //this to get the client name
        mClientDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Client client = new Client(  );
                client.setFirstName(  dataSnapshot.child(clientId).getValue(Client.class).getFirstName()  );
                client.setLastName(  dataSnapshot.child(clientId).getValue(Client.class).getLastName()  );

                eIClientNameTxt.setText( client.getFirstName() + " " + client.getLastName() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        mOrderDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get the service number and quantity for each order
                for(String oNums : orderNumsBeforeUpdate){
                    Order order = new Order();
                    order.setServiceNum( dataSnapshot.child(oNums).getValue(Order.class).getServiceNum()  );
                    order.setQuantity( dataSnapshot.child(oNums).getValue(Order.class).getQuantity() );
                    //hash table the selected services and thier quantities
                    //use this to populate the service list view
                    selectedServicesHash.put( order.getServiceNum(), order.getQuantity() );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        mServiceDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //put all the services num of the hashtable in a list
                Set<String> serviceNums= selectedServicesHash.keySet();
                //for every service selected by the user, display the name
                for(String s : serviceNums) {
                    Service service = new Service();
                    service.setServiceName( dataSnapshot.child( s ).getValue( Service.class ).getServiceName() ); //set the name
                    service.setServicePrice( dataSnapshot.child( s ).getValue( Service.class ).getServicePrice() );
                    service.setServicePriceSecCurrency( dataSnapshot.child( s ).getValue(Service.class).getServicePriceSecCurrency() );
                    service.setServicePlusProfit( dataSnapshot.child( s ).getValue(Service.class).getServicePlusProfit() );
                    //format the price in the label as(2,000,000)
                    String priceIDFormatted = formatPrice( service.getServicePriceSecCurrency() * selectedServicesHash.get(s) );
                    //take the sum of all the services and produce the total
                    //for customers
                    //multiply by the quantity of each service (1 or more)
                    totalPrice += service.getServicePriceSecCurrency()*selectedServicesHash.get(s);
                    //total expenses (original price in tr) for user
                    invoiceExpenses += service.getServicePrice()*selectedServicesHash.get(s);
                    //store the last profit value
                    //__+++++____+++++_____+++++____+++++_____+++++Test
                    invoiceProfit = Double.valueOf(eIProfitTxt.getText().toString());
                    //_____+++++____+++++_____+++++_____+++++_____Test
                    invoicePlusProfitFirstCurrence += service.getServicePlusProfit()
                            *selectedServicesHash.get(s);
                    eIServisesTxt.append( "- " + service.getServiceName() +
                            "X " +selectedServicesHash.get(s) + "\n");
                    eIServicesPriceTxt.append(priceIDFormatted  + "\n");
                    
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }
    //helping method to create, populate and showing the client dialog box
    private void createClientDialog() {

        //initializing the dialog box with the correct layout
        final Dialog dialog = new Dialog( EditInvoiceActivity.this );
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
                if (clientIdFromDialog.length() != 0) {
                    mClientDatabaseReference.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Client client = new Client();
                            client.setFirstName( dataSnapshot.child( clientIdFromDialog ).getValue( Client.class ).getFirstName() ); //set the name
                            client.setLastName( dataSnapshot.child( clientIdFromDialog ).getValue( Client.class ).getLastName() ); //set the name
                            eIClientNameTxt.setText( client.getFirstName() + " " + client.getLastName() );
                            clientId = client.getClientId();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    } );
                } else eIClientNameTxt.setText( R.string.add_client_text );

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
        eIClientListView = (ListView) dialog.findViewById( R.id.client_list_view_selector );
        mClientDialogProgressBar = (ProgressBar) dialog.findViewById( R.id.progressBar_client_selector );
        mClientDialogProgressBar.setVisibility( View.VISIBLE );
        // Initialize client ListView and its adapter
        final List<Client> clients = new ArrayList<>();
        eIClientAdapter = new ClientAdapter( EditInvoiceActivity.this, R.layout.client_item, clients );
        eIClientListView.setAdapter( eIClientAdapter );
        //attach the data base reader listener to display the latest clients records
        attachDatabaseReadListener();

        //set on item selected listener
        //on clock go back to this activity with the client Id
        eIClientListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
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



    //Show the days selector dialog and set the due date after the selected date
    private void createDaysDialog()
    {
        //change the due date
        LayoutInflater li = LayoutInflater.from( EditInvoiceActivity.this );

        //inflate the dialog view and get the RadioGroup element in it
        final View getDateSelectorView = li.inflate( R.layout.due_date_selector_dialog, null );
        final RadioGroup daysAfterRadioGroup = getDateSelectorView.findViewById( R.id.days_radio_group );
        // set dialog message
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( EditInvoiceActivity.this );

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
                        eIDueDateTxt.setText( setDueDateAfter( 0 ) );
                        break;
                    case "After 15 days":
                        eIDueDateTxt.setText( setDueDateAfter( 15 ) );
                        break;
                    case "After 30 days":
                        eIDueDateTxt.setText( setDueDateAfter( 30 ) );
                        break;
                    case "After 45 days":
                        eIDueDateTxt.setText( setDueDateAfter( 45 ) );
                        break;
                    case "After 60 days":
                        eIDueDateTxt.setText( setDueDateAfter( 60 ) );
                        break;


                }

            }
        } ).create().show();



    }

    //get the date after the desired number of days
    //this is the Due date for the invoice
    public String setDueDateAfter( int days)
    {
        String date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(eIIssueDate.getText().toString()));
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


    public void updateInvoice(){
    //Check if the client and services are selected (not empty)
        if(eIClientNameTxt.length() != 0 &&selectedServicesHash.size() != 0)
    {
        String invoiceKey = extras;
        //Create new Order object passing the information provided by the user
        //create order for every service and add it to the ordet list
        Set<String> servicesKeys = selectedServicesHash.keySet();
        for(String s: servicesKeys)
        {
            //the following will be edited and we need to do the following
            //remove the previous orders nums stored in the invoice
            //create new order list
            String keyOrder = mOrderDatabaseReference.push().getKey();

            //set the order value with the client Id and service
            // (selectedServicesHash.get(s) represent the quantity for eac service)
            Order order = new Order(keyOrder,clientId, s, selectedServicesHash.get(s) );
            orderArrayList.add(order);//use this to get total and create invoice
            //fetch order numbers from arrayList
            ArrayList<String> orderNums = new ArrayList<String>(  );
            for(Order o : orderArrayList)
            {
                orderNums.add(o.getOrderNum());
            }
            //Update the new Invoice
            invoice = new Invoice(invoiceKey, clientId, orderNums,
                    eIIssueDate.getText().toString(), eIDueDateTxt.getText().toString(), totalPrice,
                    invoiceProfit, invoiceExpenses, Integer.valueOf(eIShipmentCost.getText().toString()));
            //store order in database before invoice is saved
            mOrderDatabaseReference.child(keyOrder).setValue(order);
            //store the Invoice in the database
            mInvoiceDatabaseReference.child( invoiceKey ).setValue( invoice );
            for(String oldOrderNum:orderNumsBeforeUpdate) {
                //remove the previous list of orders
                mOrderDatabaseReference.child(oldOrderNum).removeValue();
            }

            if (eINoteTxt.getText().length() != 0)
            {
                mInvoiceDatabaseReference.child( invoiceKey ).
                        child( "invoiceNote" ).setValue( eINoteTxt.getText().toString() );
            }

            Log.v(TAG, "this the invoice expenses before update"+invoiceExpensesBeforeUpdate );
            //calculate the user total expenses (remove the previous expenses in case it was changed)
            userNewExp = (currentTotalExp - invoiceExpensesBeforeUpdate) +invoiceExpenses;
            toastMessage( "Invoice is up to date" );
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
                updateInvoice();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createServiceDialog() {
        //initializing the dialog box with the correct layout
        //change the due date
        LayoutInflater li = LayoutInflater.from( EditInvoiceActivity.this );

        //inflate the dialog view and get the RadioGroup element in it
        final View getServiceSelectorView = li.inflate( R.layout.services_selector_dialog, null );

        // set dialog message
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( EditInvoiceActivity.this );

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
        eIServiceListView = (ListView) getServiceSelectorView.findViewById( R.id.service_list_view_selector );
        mServiceDialogProgressBar = (ProgressBar) getServiceSelectorView.findViewById( R.id.progressBar_service_selector );
        mServiceDialogProgressBar.setVisibility( View.VISIBLE );
        // Initialize client ListView and its adapter
        final List<Service> services = new ArrayList<>();
        mServiceAdapter = new ServiceAdapterCheckBox( EditInvoiceActivity.this,
                R.layout.service_item_check_box, services, selectedServicesHash);
        eIServiceListView.setAdapter( mServiceAdapter );
        //mServiceListView.setItemsCanFocus( false );
        // we want multiple clicks
        eIServiceListView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
        //mServiceListView.setItemsCanFocus(false);
        //attach the data base reader listener to display the services
        attachServiceDatabaseReadListener();


        eIServiceListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //default quantity for services
                int defaultQuantity = 1;
                //find the checkedTextView inside the relative view
                ctv = (CheckedTextView) view.findViewById( R.id.checkedTextView );
                mServiceQuantity = (TextView) view.findViewById( R.id.quantity_tv );
                mMinusBtn = (Button) view.findViewById( R.id.minus_btn );
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
                    eIServisesTxt.setText( " " );
                    eIServicesPriceTxt.setText( " " );
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
                                eIServisesTxt.append( "- " + service.getServiceName() +
                                        "X " +selectedServicesHash.get(s) + "\n");
                                eIServicesPriceTxt.append(priceIDFormatted  + "\n");


                            }
                            //calculate the net profit
                            //net profit (for user)
                            invoiceProfit += (invoicePlusProfitFirstCurrence-invoiceExpenses);
                            //format the total price
                            //format the price in the label as(2,000,000)
                            String totalPriceFormatted = formatPrice( totalPrice );

                            //set the text View with the total value for each field(no shipment)
                            eISubTotal.setText( totalPriceFormatted );

                            shipmentCost = Integer.valueOf( eIShipmentCost.getText().toString() );
                            double totalPlusShipment = totalPrice + shipmentCost;
                            eITotal.setText( String.valueOf( totalPlusShipment ) );

                            eIExpensesTxt.setText( String.valueOf( invoiceExpenses ) );
                            eIProfitTxt.setText(String.valueOf(invoiceProfit));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    } );
                }
                else
                {
                    //if there is no service selected reset text views
                    eIServisesTxt.setText( R.string.service_add );
                    eIServicesPriceTxt.setText("");
                    eITotal.setText( "ID 0" );
                    eIExpensesTxt.setText( "/" );
                    eIProfitTxt.setText( "/" );
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

    private void attachDatabaseReadListener() {
        if (mChiltEventListener == null) {
            mChiltEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Client client = dataSnapshot.getValue( Client.class );
                    eIClientAdapter.add( client );
                    if(eIClientAdapter.getCount() > 0)
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

    private String formatPrice(double price){
        //format the price in the label as(2,000,000)
        DecimalFormat formatter = new DecimalFormat( "##,###,###" );
        String priceFormatted = formatter.format( price );
        return priceFormatted;
    }

    private void detachDatabaseReadListener() {
        if (mChiltEventListener != null) {

            mClientDatabaseReference.removeEventListener( mChiltEventListener );
            mChiltEventListener = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    private void toastMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }

}
