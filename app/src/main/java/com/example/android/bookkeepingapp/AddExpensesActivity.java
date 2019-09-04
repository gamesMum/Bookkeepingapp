package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddExpensesActivity extends AppCompatActivity {

    private  String TAG = "AddExpensesActivity";
    private EditText mExpenseName;
    private EditText mExpenseValuee;
    private EditText mNotes;
    private Toolbar toolbar;

    private double currentTotalExp;
    private double userNewExp;
    private User userData;

    //Firebase variables
    private  FirebaseUser user;
    private  String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mExpensesDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_expenses );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_service);
        setSupportActionBar(toolbar);
        //add (X) icon to the custom toolbar
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        intent = new Intent( this, MainActivity.class );

        userData = new User();
        //Initialize xml element
        mExpenseName = (EditText) findViewById( R.id.expenses_name );
        mExpenseValuee = (EditText) findViewById( R.id.expense_value );
        mNotes = (EditText) findViewById( R.id.expenses_notes );

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "user" );
            //store the data under loggedin user Id
            mExpensesDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "expense" );
        }


        //close this activity when we press (X) icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Go back to Expenses fragment
            intent.putExtra("fragmentName","expenseFragment");
            startActivity(intent);
            }
        });


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

    public void createNewExpense() {
        //get the elements in the dialog
        String expenseName = mExpenseName.getText().toString();
        double expensesValue =  Double.parseDouble(mExpenseValuee.getText().toString());
        String expenseNotes = mNotes.getText().toString();
        String issueDate = getCurrentDate();
        //Check if the user enterd the service name and price
        if (expenseName.trim().length() > 0 && expensesValue > 0) {
            String key = mExpensesDatabaseReference.push().getKey();

            Expense expense = new Expense(key, expenseName,expensesValue, issueDate);
            //set the necessary inforatio and store in database
            mExpensesDatabaseReference.child(key).setValue(expense);
            //set the notes if any
            mExpensesDatabaseReference.child(key).child( "expensesNotes" ).setValue(expenseNotes);
            //calculate the user total expenses
            userNewExp = currentTotalExp + expensesValue;
            toastMessage("New expense has been added.");
            //Update the user expenses by updating the currentTotalExp to the userNewExp
            mUserDatabaseReference.child( "totalExpenses" ).setValue( userNewExp );
            mExpenseName.setText("");
            mExpenseValuee.setText("");
            mNotes.setText( "" );


        } else {
            //else tell the user that there is an error
            toastMessage( "did you forget to put expense name or value" );
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
                createNewExpense();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

}
