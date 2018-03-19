package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ViewClientActivity extends AppCompatActivity {

    private TextView mClientName;
    private String extras;

    // Firebase instance variables
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClientDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_client );

        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClientDatabaseReference = mFirebaseDatabase.getReference().child( "client" );

        mClientName = (TextView) findViewById( R.id.name_text );

        mClientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               Client client = new Client(  );
               client.setFirstName( dataSnapshot.child(extras).getValue(Client.class).getFirstName()); //set the name
                client.setLastName( dataSnapshot.child(extras).getValue(Client.class).getLastName()); //set the name
                mClientName.setText( client.getFirstName() + " " + client.getLastName() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        extras = getIntent().getStringExtra("clientId");
        if (extras != null) {
            //Show Client Name

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Go back to client fragment
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("fragmentName","clientFragment"); //for example
        startActivity(intent);
    }
}
