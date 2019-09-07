package com.example.android.bookkeepingapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddProfitActivity extends AppCompatActivity {

    private  String TAG = "AddProfitActivity";
    private EditText mProfitName;
    private EditText mProfitValuee;
    private EditText mNotes;
    private Toolbar toolbar;

    private double currentTotalPro;
    private double userNewPro;
    private User userData;

    //Firebase variables
    private  FirebaseUser user;
    private  String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mProfitDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_profit );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_service);
        setSupportActionBar(toolbar);
        //add (X) icon to the custom toolbar
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        intent = new Intent( this, MainActivity.class );

        userData = new User();
        //Initialize xml element
        mProfitName = (EditText) findViewById( R.id.profit_name );
        mProfitValuee = (EditText) findViewById( R.id.profit_value );
        mNotes = (EditText) findViewById( R.id.profit_notes );

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "user" );
            //store the data under loggedin user Id
            mProfitDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "profit" );
        }


        //close this activity when we press (X) icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Go back to Expenses fragment
                intent.putExtra("fragmentName","profitFragment");
                startActivity(intent);
            }
        });


        //get the current User expenses (before invoice is created)
        mUserDatabaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the current total expenses for the user
                userData.setTotalProfit( dataSnapshot.getValue( User.class ).getTotalProfit() ); //set the ecp

                currentTotalPro = userData.getTotalProfit();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

    }

    public void createNewProfit() {
        //get the elements in the dialog
        String profitName = mProfitName.getText().toString();
        double profitValue =  Double.parseDouble(mProfitValuee.getText().toString());
        String profitNotes = mNotes.getText().toString();
        String issueDate = getCurrentDate();
        //Check if the user enterd the service name and price
        if (profitName.trim().length() > 0 && profitValue > 0) {
            String key = mProfitDatabaseReference.push().getKey();

            Profit profit = new Profit(key, profitName,profitValue, issueDate);
            //set the necessary inforatio and store in database
            mProfitDatabaseReference.child(key).setValue(profit);
            //set the notes if any
            mProfitDatabaseReference.child(key).child( "profitNote" ).setValue(profitNotes);
            //calculate the user total expenses
            userNewPro = currentTotalPro + profitValue;
            toastMessage("New profit has been added.");
            //Update the user expenses by updating the currentTotalExp to the userNewExp
            mUserDatabaseReference.child( "totalProfit" ).setValue( userNewPro );
            mProfitName.setText("");
            mProfitValuee.setText("");
            mNotes.setText( "" );


        } else {
            //else tell the user that there is an error
            toastMessage( "did you forget to put profit name or value" );
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
                createNewProfit();
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
