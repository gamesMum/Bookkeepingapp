package com.example.android.bookkeepingapp;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditExpenseActivity extends AppCompatActivity {


    private  String TAG = "EditExpenseActivity";
    private EditText mExpenseDesc;
    private EditText mExpenseValue;
    private EditText mExpenseNote;
    private String expenseIssueDate;
    private Toolbar toolbar;
    private Intent intent;
    private double currentTotalExp;
    private double userNewExp;

    //initialized only once
    private static double  thisExpensesBeforeUpdate;

    private String extras;


    private String clientID;

    //Firebase variables
    private User userData;
    private FirebaseUser user;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mExpenseDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_expense );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_edit_expense);
        setSupportActionBar(toolbar);
        userData = new User();
        toolbar.setTitle(getString(R.string.clients_edit));
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        intent = new Intent(this, MainActivity.class);
        ActionBar actionbar = getSupportActionBar();

        //Initialize xml element
        mExpenseDesc = (EditText) findViewById( R.id.expense_name_edit );
        mExpenseValue = (EditText) findViewById( R.id.expense_value_edit );
        mExpenseNote = (EditText) findViewById( R.id.expense_notes_edit );

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            mExpenseDatabaseReference = mFirebaseDatabase.getReference().child( userID ).child( "expense" );
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "user" );
        }

        //take the client Id selected from the previous activity
        extras = getIntent().getStringExtra("expenseNum");
        if (extras != null) {
            //Show Client Name

        }

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


        mExpenseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Expense expense = new Expense(  );
                expense.setExpenseNum( dataSnapshot.child(extras).getValue(Expense.class).getExpenseNum() );
                expense.setExpenseName( dataSnapshot.child(extras).getValue(Expense.class).getExpenseName());
                expense.setExpenseValue( dataSnapshot.child(extras).getValue(Expense.class).getExpenseValue());
                expense.setIssueDate( dataSnapshot.child(extras).getValue(Expense.class).getIssueDate() );

                expense.setExpenseNote( dataSnapshot.child(extras).getValue(Expense.class).getExpenseNote());

                mExpenseValue.setText( String.valueOf( expense.getExpenseValue() ));
                mExpenseDesc.setText( expense.getExpenseName() );
                //get the expense value before update and keep it
                thisExpensesBeforeUpdate = expense.getExpenseValue();

                if(expense.getExpenseNote() != null) {
                    mExpenseNote.append( expense.getExpenseNote() );
                }
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

    }

    public void updateExpense() {
        //get the elements in the dialog
        String expenseDesc = mExpenseDesc.getText().toString();
        double expenseValue = Double.valueOf( mExpenseValue.getText().toString() );
        String expenseNote = mExpenseNote.getText().toString();

        //Check if the user enterd either the first or the last name
        if (expenseDesc.trim().length() > 0 || expenseValue > 0) {
            //set the values at the same clientId selected (update)
            String key = extras;
            Expense expense = new Expense(key, expenseDesc,expenseValue, expenseIssueDate);
            mExpenseDatabaseReference.child(key).setValue(expense);
            mExpenseDatabaseReference.child(key).child( "expensesNotes" ).setValue(expenseNote);

            toastMessage("data is up to date.");
            mExpenseDesc.setText("");
            mExpenseValue.setText("");
            mExpenseNote.setText( "" );

            //Update total expense
            //calculate the user total expenses (remove the previous expenses in case it was changed)
            userNewExp = (currentTotalExp - thisExpensesBeforeUpdate) +expenseValue;
            toastMessage( "Invoice is up to date" );
            //Update the user expenses by updating the currentTotalExp to the userNewExp
            mUserDatabaseReference.child( "totalExpenses" ).setValue( userNewExp );
            //Go back to client fragment
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("fragmentName","expenseFragment"); //for example
            startActivity(intent);

        } else {
            //else tell the user that there is an error
            toastMessage( "Enter expense information!" );
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
                updateExpense();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
