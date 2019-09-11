package com.example.android.bookkeepingapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

public class ViewInvoiceActivity extends AppCompatActivity {

    private String TAG = "ViewInvoiceActivity";
    private final int PAID = 1;
    private final int NOTPAID = 0;
    private Toolbar toolbar;
    private TextView mClientName;
    private TextView mIssueDate;
    private TextView mDueDate;
    private TextView mServices;
    private TextView mServicePrice;
    private TextView mNote;
    private TextView mTotal;
    private TextView mSubTotal;
    private TextView mShipmentCost;
    private TextView mInvoiceNum;
    private TextView mInvoiceProfit;
    private TextView mInvoiceExpenses;

    //for pdf
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File newpdfFile;

    private Menu menu;
    private TextView mNotificationStripe;

    private boolean isOverDue;

    //the client Id and order number stored in invoice
    private String mClientId;
    private ArrayList<String> mOrderNum;
    private ArrayList<String> mServiceNum;
    private Invoice invoice;
    private User userData;
    private String extras;
    private double userCurrentExp;
    private double userCurrentProfit;
    private double newUserProfit;
    private double newUserExp;

    // Firebase instance variables
    private FirebaseUser user;
    private  String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mInvoiceDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mClientDatabaseReference;
    private DatabaseReference mOrderDatabaseReference;
    private DatabaseReference mServiceDatabaseReference;
    private ValueEventListener mInvoiceDataReferenceEventListener;
    private ValueEventListener mOrderDataReferenceEventListener;

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
        //enable back navigation icon for costume toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userData = new User(  );


        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under logged in user Id
            mInvoiceDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "invoice" );
            mClientDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "client" );
            mOrderDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "order" );
            mServiceDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "service" );
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "user" );
            //For offline sync of data
            mInvoiceDatabaseReference.keepSynced(true);
        }

        //find the view in the xml layout
        mClientName =  findViewById( R.id.client_invoice_view_text );
        mDueDate =  findViewById( R.id.due_date_invoice_view_value );
        mIssueDate =  findViewById( R.id.issue_date_text_invoice_view_value );
        mServices = findViewById( R.id.services_invoice_view );
        mServicePrice =  findViewById( R.id.services_add_price_invoice_view );
        mNote =  (TextView) findViewById( R.id.invoice_notes_view_txt );
        mInvoiceNum=  findViewById( R.id.invoice_number_invoice_view_value );
        mTotal =  findViewById( R.id.invoice_total_value_view );
        mSubTotal = (TextView)findViewById( R.id.invoice_sub_total_value_view );
        mShipmentCost = (TextView) findViewById( R.id.view_invoice_shipment_value_view );
        mInvoiceProfit  =  findViewById( R.id. invoice_profit_value_view);
        mInvoiceExpenses =  findViewById( R.id.invoice_expenses_value_view );

        mNotificationStripe = (TextView) findViewById( R.id.notification_stripe );

        mOrderNum = new ArrayList<String>(  );
        mServiceNum = new ArrayList<String>(  );

        mOrderDataReferenceEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(String o : mOrderNum) {
                    Order order = new Order();
                    order.setServiceNum( dataSnapshot.child( o ).getValue( Order.class ).getServiceNum() ); //set the NUM
                    //get the list of services in each order
                    mServiceNum.add( order.getServiceNum() );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mInvoiceDataReferenceEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                invoice = new Invoice();

                invoice.setDueDate( dataSnapshot.child( extras ).getValue( Invoice.class ).getDueDate() ); //set the name
                invoice.setIssueDate( dataSnapshot.child( extras ).getValue( Invoice.class ).getIssueDate() ); //set the name
                invoice.setInvoiceNumber( dataSnapshot.child( extras ).getValue( Invoice.class ).getInvoiceNumber() );
                invoice.setPaid( dataSnapshot.child( extras ).getValue( Invoice.class ).getPaid() );
                invoice.setOrderNums( dataSnapshot.child( extras ).getValue( Invoice.class ).getOrderNums() );
                invoice.setClientID( dataSnapshot.child( extras ).getValue( Invoice.class ).getClientID() );
                invoice.setTotal( dataSnapshot.child( extras ).getValue( Invoice.class ).getTotal() );
                invoice.setInvoiceExpenses( dataSnapshot.child( extras ).getValue( Invoice.class ).getInvoiceExpenses() );
                invoice.setInvoiceProfit( dataSnapshot.child( extras ).getValue( Invoice.class ).getInvoiceProfit() );
                invoice.setInvoiceNote( dataSnapshot.child( extras ).getValue( Invoice.class ).getInvoiceNote() );
                invoice.setShippingCost( dataSnapshot.child( extras ).getValue( Invoice.class ).getShippingCost() );
                keyInvoice = invoice.getInvoiceNumber();
                //check if it is paid or not
                mInvoiceStatus = invoice.getPaid();


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

                if (invoice.getInvoiceNote() != null) {
                    mNote.setText( invoice.getInvoiceNote() );
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
                mSubTotal.setText( formatPrice( invoice.getTotal() ) );

                mShipmentCost.setText(String.valueOf( invoice.getShippingCost()  ) );
                double totalPlusShipment = invoice.getTotal() +  invoice.getShippingCost();
                mTotal.setText( String.valueOf( totalPlusShipment ) );
                mInvoiceProfit.setText( String.valueOf( invoice.getInvoiceProfit() ) );
                mInvoiceExpenses.setText( String.valueOf( invoice.getInvoiceExpenses() ) );


                //Check if the invoice is paid or not
                updateInvoiceStatus();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        mInvoiceDatabaseReference.addValueEventListener(mInvoiceDataReferenceEventListener);
        //get current profit and expenses for the user
        mUserDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO:check if this work
                //get the current total expenses for the user
                userData.setTotalExpenses( dataSnapshot.getValue( User.class ).getTotalExpenses() ); //set the exp
                userData.setTotalProfit( dataSnapshot.getValue(User.class).getTotalProfit());
                //add the sum of this invoice expenses to the current user's
                //expenses
                //calculate the total expenses
                // double newExpenses  = invoiceExpenses + userData.getTotalExpenses();
                Log.v(TAG, "The current value of the  expenses: " + userData.getTotalExpenses());
                Log.v(TAG, "The current value of the  profits: " + userData.getTotalProfit());
                userCurrentExp = userData.getTotalExpenses();
                userCurrentProfit = userData.getTotalProfit();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

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
        mOrderDatabaseReference.addValueEventListener(mOrderDataReferenceEventListener);
        //get the client name
        mServiceDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(String s : mServiceNum) {
                    Service service = new Service();
                    service.setServiceName( dataSnapshot.child( s ).getValue( Service.class ).getServiceName() ); //set the name
                    service.setServicePriceSecCurrency( dataSnapshot.child( s ).getValue( Service.class ).getServicePriceSecCurrency() );

                    mServices.append( "- " + service.getServiceName() + "\n");
                    mServicePrice.append("ID " + formatPrice( service.getServicePriceSecCurrency() )+ "\n");
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

    //this is the physical back (on the actual phone)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Go back to client fragment
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("fragmentName","invoiceFragment"); //for example
        startActivity(intent);
    }

    //Change the stripe color
    private void updateInvoiceStatus() {

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
        this.menu = menu;
        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.invoice_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Go back to invoice fragment
                Intent intent = new Intent( this, MainActivity.class );
                intent.putExtra( "fragmentName", "invoiceFragment" ); //for example
                startActivity( intent );
                return true;
            case R.id.action_delete:
                //your code here
                //Add dialog box
                //remove the invoice and it's list of orders
                //show confirmation dialog
                if (mInvoiceStatus == 0) {
                    deleteInvoiceSafely();
                } else {
                    toastMessage( "Invoice must be UNPAID first!" );
                }

                return true;
            case R.id.action_edit:
                if (mInvoiceStatus == 0)//invoice is not paied
                {
                    //View edit activity
                    //return the object in the list View
                    String invoiceId = extras;
                    Intent i = new Intent( ViewInvoiceActivity.this, EditInvoiceActivity.class );
                    //pass the client Id to the next activity
                    i.putExtra( "invoiceId", invoiceId );
                    startActivity( i );
                } else {
                    toastMessage( "You ca't edit a paid invoice!" );
                }
                return true;
            case R.id.action_pay:
                //check if it is not already paied
                if (invoice.getPaid() == 0) {
                    //mark this invoice as paid
                    invoice.setPaid( PAID );
                    mInvoiceDatabaseReference.child( keyInvoice ).setValue( invoice );
                    //Update the user expenses and profit
                    //Add the invoice value to user profit (current profit plus the new paid invoice)
                    newUserProfit = userCurrentProfit + invoice.getInvoiceProfit();
                    //update user data
                    mUserDatabaseReference.child( "totalProfit" ).setValue( newUserProfit );
                    //change the stripe color
                    updateInvoiceStatus();
                    toastMessage( "This invoice is Paid" );//store the Invoice in the database
                    item.setVisible( false );//hide the invoice mark as paid
                    //show the mark Not paid instead
                    item = menu.findItem( R.id.action_un_pay );
                    item.setVisible( true );
                } else {
                    toastMessage( "This invoice is already Paid" );
                    //hide the set is paid option
                    item.setVisible( false );//hide the invoice mark as paid
                    //show the mark Not paid instead
                    item = menu.findItem( R.id.action_un_pay );
                    item.setVisible( true );
                }
                return true;

            case R.id.action_un_pay:
                //mark this invoice as unpaid
                invoice.setPaid( NOTPAID );
                mInvoiceDatabaseReference.child( keyInvoice ).setValue( invoice );
                //Update the user expenses and profit
                //subtract the invoice value from user profit
                newUserProfit = userCurrentProfit - invoice.getInvoiceProfit();
                //update user data
                mUserDatabaseReference.child( "totalProfit" ).setValue( newUserProfit );
                //change the stripe color
                updateInvoiceStatus();
                toastMessage( "This invoice is Not Paid" );//store the Invoice in the database
                item.setVisible( false );//hide the invoice mark as paid
                //show the mark as paid instead
                item = menu.findItem( R.id.action_pay );
                item.setVisible( true );
                return true;

            case R.id.action_save_pdf:
                //save the invoice as pdf file on the device
                try {
                    createPdfWrapper();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteInvoiceSafely() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //subtract the expenses from the total expenses (since the invoice is not really valid)
                newUserExp = userCurrentExp - invoice.getInvoiceExpenses();
                //update user data
                mUserDatabaseReference.child( "totalExpenses" ).setValue( newUserExp );

                mInvoiceDatabaseReference.removeEventListener(mInvoiceDataReferenceEventListener);
                mOrderDatabaseReference.removeEventListener( mOrderDataReferenceEventListener );
                //delete the invoice first
                mInvoiceDatabaseReference.child( keyInvoice ).removeValue();
                for(String OrderNum:mOrderNum) {
                    //remove the previous list of orders
                    mOrderDatabaseReference.child(OrderNum).removeValue();
                }
                toastMessage("deleted successfully!");
                //Go back to Invoice fragment
                Intent intent = new Intent( ViewInvoiceActivity.this, MainActivity.class );
                intent.putExtra("fragmentName","invoiceFragment"); //for example
                startActivity(intent);
        }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
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

    private String formatPrice(double price){
        //format the price in the label as(2,000,000)
        DecimalFormat formatter = new DecimalFormat( "##,###,###" );
        String priceFormatted = formatter.format( price );
        return priceFormatted;
    }


    //for creating and saving pdf
    private void createPdfWrapper() throws FileNotFoundException, DocumentException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale( Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            createPdf();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        File pdfPath = new File(this.getFilesDir(), "docs");
        String pdfName = "default_doc.pdf";

        boolean isPresent = true;

        if (!pdfPath.exists()) {
            isPresent = pdfPath.mkdirs();
        }
        if(isPresent) {
           // Log.v(TAG, "found directory" + docsFolder.toString());
            newpdfFile = new File(pdfPath, pdfName);
            //Uri contentUri = getUriForFile(this, "com.example.android.bookkeepingapp", pdfFile);
            OutputStream output = new FileOutputStream(newpdfFile);
            Document document = new Document( PageSize.A4 );
            PdfPTable table = new PdfPTable( new float[]{3, 3, 3, 3, 3} );
            table.getDefaultCell().setHorizontalAlignment( Element.ALIGN_CENTER );
            table.getDefaultCell().setFixedHeight( 50 );
            table.setTotalWidth( PageSize.A4.getWidth() );
            table.setWidthPercentage( 100 );
            table.getDefaultCell().setVerticalAlignment( Element.ALIGN_MIDDLE );
            table.addCell( "Name" );
            table.addCell( "Price" );
            table.addCell( "Type" );
            table.addCell( "URL" );
            table.addCell( "Date" );
            table.setHeaderRows( 1 );
            PdfPCell[] cells = table.getRow( 0 ).getCells();
            for (int j = 0; j < cells.length; j++) {
                cells[j].setBackgroundColor( BaseColor.GRAY );
            }
            //here I should retrieve my data
      /*  for (int i = 0; i < MyList1.size(); i++) {
            name = MyList1.get(i);
            type = MyList1.get(i);
            date = MyList1.get(i);
            url = MyList1.get(i);
            price = MyList1.get(i);
            String namen = name.getItem_name();
            String pricen = price.getItem_price();
            String daten = date.getCreatedAt();
            String typen = type.getItem_type_code();
            String urln = url.getItem_URL();

            table.addCell(String.valueOf(namen));
            table.addCell(String.valueOf(pricen));
            table.addCell(String.valueOf(typen));
            table.addCell(String.valueOf(urln));
            table.addCell(String.valueOf(daten.substring(0, 10)));

        }*/
            table.addCell( String.valueOf( "Burda" ) );
            table.addCell( String.valueOf( "8,000" ) );
            table.addCell( String.valueOf( "" ) );
            table.addCell( String.valueOf( "" ) );
            table.addCell( String.valueOf( "" ) );

//        System.out.println("Done");

            PdfWriter.getInstance( document, output );

            document.open();
            Font f = new Font( Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE );
            Font g = new Font( Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE );
            document.add( new Paragraph( "Pdf Data \n\n", f ) );
            document.add( new Paragraph( "Pdf File Through Itext", g ) );
            document.add( table );

//        for (int i = 0; i < MyList1.size(); i++) {
//            document.add(new Paragraph(String.valueOf(MyList1.get(i))));
//        }
            document.close();
            // Log.e("safiya", MyList1.toString());
            previewPdf();
        }
        else{
            Log.v(TAG, "can't find directory");
        }
    }

    private void previewPdf() {

        PackageManager packageManager = this.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");


        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri contentUri = getUriForFile(ViewInvoiceActivity.this, BuildConfig.APPLICATION_ID, newpdfFile);
            intent.setDataAndType(contentUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            this.startActivity(intent);
        } else {
            Toast.makeText(this, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }

}
