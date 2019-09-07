package com.example.android.bookkeepingapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummeryFragment extends Fragment {

    private String TAG = "SummeryFragment";
    private double totalProfit;
    private double totalExpenses;
    private TextView mProfitText;
    private TextView mOverdueInvoiceText;
    private TextView mDueDateInvoiceText;
    private TextView mExpenseText;
    private TextView mAddExpenseTxt;

    private String currentDate;
    private int overdueInvoiceCount;
    private int dueDateInvoiceCount;
    SimpleDateFormat sdf;
    Date DueDateF;
    Date currentDateF;

    // Firebase instance variables
    private FirebaseUser user;
    private  String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mInvoiceDatabaseReference;


    public SummeryFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get the current date to check number of due and over due invoices
        currentDate = getCurrentDate();
        sdf = new SimpleDateFormat( "yyyy / MM / dd" );
        dueDateInvoiceCount = 0;
        overdueInvoiceCount = 0;

        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_summery, container, false );

        mProfitText = (TextView) rootView.findViewById( R.id.profit_txt );
        mDueDateInvoiceText = (TextView) rootView.findViewById( R.id.invoice_due_date_text);
        mOverdueInvoiceText = (TextView) rootView.findViewById( R.id.invoice_over_due_text);
        mExpenseText = (TextView) rootView.findViewById( R.id.expense_txt );
        mAddExpenseTxt = (TextView) rootView.findViewById( R.id.add_expense_text );


        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            mUserDatabaseReference = mFirebaseDatabase.getReference().
                    child(userID).child( "user" );
            mInvoiceDatabaseReference =mFirebaseDatabase.getReference().
                    child( userID ).child( "invoice" );
            //For offline sync of data
            mUserDatabaseReference.keepSynced(true);
            mInvoiceDatabaseReference.keepSynced( true );
        }

        mAddExpenseTxt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the expense fragment
                //go to Add new service activity
                Intent i = new Intent(getActivity(), MainActivity.class );
                i.putExtra( "fragmentName", "expenseFragment");
                startActivity( i );
            }
        } );
        mInvoiceDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get the number of invoices that are over due or due date
                for(DataSnapshot val : dataSnapshot.getChildren())
                {
                    //if the invoice is not paid
                    if(Integer.valueOf( val.child( "paid" ).getValue().toString() ) == 0){
                    //get the due date for each invoice and compare it with today date
                    try {
                         currentDateF = sdf.parse( currentDate );
                         DueDateF = sdf.parse( val.child( "dueDate" ).getValue().toString() );
                        Log.v(TAG, "current date is: " + "'" +currentDateF+"'");
                        Log.v(TAG, "invoice date is: " + "'" +DueDateF+"'");
                    }catch (ParseException e) {
                        e.printStackTrace();

                    }
                        if(DueDateF.getTime() == currentDateF.getTime() )
                        {
                            //count it in as due Date invoice
                            dueDateInvoiceCount++;
                        }
                        else if(DueDateF.getTime() < currentDateF.getTime()){
                            //count it as over due invoice
                            overdueInvoiceCount++;
                        }


                }
                    else{
                        Log.v(TAG, "all invoices are paid: ");
                    }
                    mDueDateInvoiceText.setText( String.valueOf(  dueDateInvoiceCount)  );
                    mOverdueInvoiceText.setText(  String.valueOf(overdueInvoiceCount) );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        //get current profit and expenses for the user
        mUserDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //read the total profit and expenses and display them for the user
                User userApp = new User(  );
                userApp.setTotalProfit( dataSnapshot.getValue(User.class).getTotalProfit()); //set the name
                totalProfit = userApp.getTotalProfit();
                userApp.setTotalExpenses( dataSnapshot.getValue(User.class).getTotalExpenses());
                totalExpenses = userApp.getTotalExpenses();
                //set the text view with the current profit value for the user
                mProfitText.setText( String.valueOf( totalProfit )  );
                mExpenseText.setText( String.valueOf( totalExpenses ) );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


        // Inflate the layout for this fragment
        return rootView;
    }

    private String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd");
        String strDate = mdformat.format(calendar.getTime());

        return strDate;
    }


}
