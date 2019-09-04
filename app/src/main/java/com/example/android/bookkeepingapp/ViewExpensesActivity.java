package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.text.DecimalFormat;

public class ViewExpensesActivity extends AppCompatActivity {

    private String TAG = "ViewExpenseActivity";
    private Toolbar toolbar;
    private TextView mExpenseName;
    private TextView mExpenseValue;
    private double mUserCurrentExp;
    private TextView mExpenseNotes;
    private String extras;
    private TextView mIssueDate;
    private Intent intent;

    // Firebase instance variables
    private FirebaseUser user;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mExpenseDatabaseReference;
    public ValueEventListener databaseEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_expenses );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById( R.id.toolbar_expense_view );
        setSupportActionBar( toolbar );
        toolbar.setTitle( getString( R.string.expendings_text ) );
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        //enable back navigation icon for costume toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Expense expense = new Expense();
                expense.setExpenseName( dataSnapshot.child( extras ).getValue( Expense.class ).getExpenseName() ); //set the name
                expense.setExpenseValue( dataSnapshot.child( extras ).getValue( Expense.class ).getExpenseValue() ); //set the price
                expense.setExpenseNote( dataSnapshot.child( extras ).getValue( Expense.class ).getExpenseNote() );
                expense.setIssueDate(dataSnapshot.child( extras ).getValue( Expense.class ).getIssueDate()  );
                //append the right values for each textView in xml view
                mExpenseName.append( " " + expense.getExpenseName() );
                mExpenseValue.append(" " + String.valueOf(  expense.getExpenseValue() ));
                mIssueDate.setText( expense.getIssueDate() );
                if(expense.getExpenseNote() != null) {
                    mExpenseNotes.append(expense.getExpenseNote() );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } ;
        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mExpenseDatabaseReference = mFirebaseDatabase.getReference().child( userID ).child( "expense" );
            //For offline sync of data
            mExpenseDatabaseReference.keepSynced( true );
        }

        mExpenseName = (TextView) findViewById( R.id.expense_name_text_view );
        mExpenseValue = (TextView) findViewById( R.id.expense_value_view );
        mExpenseNotes = (TextView) findViewById( R.id.expense_notes_text_view );
        mExpenseDatabaseReference.addValueEventListener(databaseEventListener);
        mIssueDate = (TextView) findViewById( R.id.issue_date_ex_tv );
        //get the expense number from the ExpenseFragment
        extras = getIntent().getStringExtra( "expenseNum" );
        if (extras != null) {
            //Show Client Name

        }

      /*  toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               intent = new Intent( view.getContext(), MainActivity.class );
               intent.putExtra( "fragmentName", "serviceFragment" );
               startActivity( intent );
            }
        } );*/



        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    // User is signed out
                    //go to login activity
                    Intent i = new Intent( ViewExpensesActivity.this, LoginActivity.class );
                    startActivity( i );
                    toastMessage( "Successfully signed out" );
                }
            }
        };
    }

    //this is the physical back (on the actual phone)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Go back to service fragment
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("fragmentName","serviceFragment"); //for example
        startActivity(intent);
    }

    private void DeleteExpense(String ExpenseNum){
        mExpenseDatabaseReference.child( ExpenseNum ).setValue( null );
        //remove the value event listener
        mExpenseDatabaseReference.removeEventListener(databaseEventListener );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.view_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Go back to service fragment
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("fragmentName","expenseFragment"); //for example
                startActivity(intent);
                return true;
            case R.id.action_delete:
                //your code here
                //Add dialog box
                if(extras != null) {
                    //delete thi service
                    DeleteExpense( extras );
                    finish();
                }
                Log.v( TAG, "Oops you just deleted me!" );
                return true;
            case R.id.action_edit:
                //View edit activity
                //return the object in the list View
                String expenseNum = extras;
                Intent i = new Intent( ViewExpensesActivity.this, EditExpenseActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "expenseNum", expenseNum );
                startActivity( i );
                Log.v( TAG, "OK edit me now!" );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }

}
