package com.example.android.bookkeepingapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Save firebase configurations
 */
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseConfig {
    // Firebase instance variables
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClientDatabaseReference;

    public FirebaseDatabaseConfig(String userID, String dataChild ) {
        // Initialize Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mClientDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( dataChild );
        //For offline sync of data
        mClientDatabaseReference.keepSynced(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();
    }

    public DatabaseReference getmClientDatabaseReference() {
        return mClientDatabaseReference;
    }

    public FirebaseDatabase getmFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }
}
