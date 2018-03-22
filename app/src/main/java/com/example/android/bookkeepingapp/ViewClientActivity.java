package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewClientActivity extends AppCompatActivity {

    private String TAG = "ViewClientActivity";
    private Toolbar toolbar;
    private TextView mClientName;
    private TextView mCompany;
    private String extras;

    // Firebase instance variables
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClientDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_client );

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.clints_text));
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);

        ActionBar actionbar = getSupportActionBar();

        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClientDatabaseReference = mFirebaseDatabase.getReference().child( "client" );

        mClientName = (TextView) findViewById( R.id.name_text_view );
        mCompany = (TextView) findViewById( R.id.company_text_view );

        mClientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               Client client = new Client(  );
               client.setFirstName( dataSnapshot.child(extras).getValue(Client.class).getFirstName()); //set the name
                client.setLastName( dataSnapshot.child(extras).getValue(Client.class).getLastName()); //set the name
                mClientName.setText( client.getFirstName() + " " + client.getLastName() );
                client.setCompanyName( dataSnapshot.child(extras).getValue(Client.class).getCompanyName());
                mCompany.setText( client.getCompanyName() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        extras = getIntent().getStringExtra("clientId");
        if (extras != null) {
            //Show Client Name

        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Go back to client fragment
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("fragmentName","clientFragment"); //for example
        startActivity(intent);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the edit_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_menu, menu);
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
                Log.v(TAG, "Oops you just deleted me!");
                return true;
            case R.id.action_edit:
                //View edit activity
                //return the object in the list View
                String clientID = extras;
                Intent i = new Intent( ViewClientActivity.this, EditClientActivity.class );
                //pass the client Id to the next activity
                i.putExtra( "clientId", clientID );
                startActivity( i );
                Log.v(TAG, "OK edit me now!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
