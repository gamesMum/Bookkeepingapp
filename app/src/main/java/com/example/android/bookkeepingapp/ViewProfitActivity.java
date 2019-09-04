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

public class ViewProfitActivity extends AppCompatActivity {

    private String TAG = "ViewProfıtActivity";
    private Toolbar toolbar;
    private TextView mProfitName;
    private TextView mProfitValue;
    private TextView mProfitNotes;
    private String extras;
    private TextView mIssueDate;
    private Intent intent;

    // Firebase instance variables
    private FirebaseUser user;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProfitDatabaseReference;
    public ValueEventListener databaseEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_profit );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById( R.id.toolbar_profit_view );
        setSupportActionBar( toolbar );
        toolbar.setTitle( getString( R.string.view_profit ) );
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        //enable back navigation icon for costume toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (extras != null) {
                    Profit profit = new Profit();
                    profit.setProfitName( dataSnapshot.child( extras ).getValue( Profit.class ).getProfitName() ); //set the name
                    profit.setProvitValue( dataSnapshot.child( extras ).getValue( Profit.class ).getProfitValue() ); //set the price
                    profit.setProfitNote( dataSnapshot.child( extras ).getValue( Profit.class ).getProfitNote() );
                    profit.setIssueDate( dataSnapshot.child( extras ).getValue( Profit.class ).getIssueDate() );

                    //append the right values for each textView in xml view
                    mProfitName.append( " " + profit.getProfitName() );
                    mProfitValue.append( " " + String.valueOf( profit.getProfitValue() ) );
                    mIssueDate.setText( profit.getIssueDate() );
                    if (profit.getProfitNote() != null) {
                        mProfitNotes.append( " " + profit.getProfitNote() );
                    }
                }else
                    Log.v(TAG, "there is no number!!");
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
            mProfitDatabaseReference = mFirebaseDatabase.getReference().child( userID ).child( "profit" );
            //For offline sync of data
            mProfitDatabaseReference.keepSynced( true );
        }


        //get the expense number from the ExpenseFragment
        extras = getIntent().getStringExtra( "ProfitNum" );
        if (extras != null) {
            //Show Client Name
            Log.v(TAG, "extras = "+ extras);
        }
        mProfitName = (TextView) findViewById( R.id.profit_name_text_view );
        mProfitValue = (TextView) findViewById( R.id.profit_value_view );
        mProfitNotes = (TextView) findViewById( R.id.profit_notes_text_view );
        mIssueDate = (TextView) findViewById( R.id.issue_date_pro_tv );
        mProfitDatabaseReference.addValueEventListener(databaseEventListener);

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
                    Intent i = new Intent( ViewProfitActivity.this, LoginActivity.class );
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
        intent.putExtra("fragmentName","profitFragment"); //for example
        startActivity(intent);
    }

    private void DeleteProfit(String ProfitNum){

        //delete the service
        mProfitDatabaseReference.child( ProfitNum ).setValue( null );
        //remove the value event listener
        mProfitDatabaseReference.removeEventListener(databaseEventListener );

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
                intent.putExtra("fragmentName","profitFragment"); //for example
                startActivity(intent);
                return true;
            case R.id.action_delete:
                //your code here
                //Add dialog box
                if(extras != null) {
                    //delete thi service
                    DeleteProfit( extras );
                    finish();
                }
                Log.v( TAG, "Oops you just deleted me!" );
                return true;
            case R.id.action_edit:
                //View edit activity
                //return the object in the list View
                String profitNum = extras;
                Intent i = new Intent( ViewProfitActivity.this, EditProfitActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "profitNum", profitNum );
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
