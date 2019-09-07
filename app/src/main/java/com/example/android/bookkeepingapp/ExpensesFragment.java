package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ExpensesFragment extends Fragment {
    private static final String TAG = "ExpenseFragment";

    //expense xml attributes
    private ListView mExpenseListView;
    private ExpenseAdapter mExpenseAdapter;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddExpenseFab;
    private ChildEventListener mChildEventListener;


    private FirebaseUser user;
    private  String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // Firebase instance variables
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mExpenseDatabaseReference;
    public ExpensesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //find and inflate view
        final View rootView = inflater.inflate( R.layout.fragment_expenses, container, false );

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userID = user.getUid();
            //store the data under loggedin user Id
            mExpenseDatabaseReference = mFirebaseDatabase.getReference().child(userID).child( "expense" );
            //For offline sync of data
            mExpenseDatabaseReference.keepSynced(true);
        }

        // Initialize references to views
        mProgressBar = (ProgressBar) rootView.findViewById( R.id.expense_progressBar );
        mExpenseListView = (ListView) rootView.findViewById( R.id.expense_list_view );
        mAddExpenseFab = (FloatingActionButton) rootView.findViewById( R.id.expense_fab);

        // Initialize message ListView and its adapter
        final List<Expense> expenses = new ArrayList<>();
        mExpenseAdapter = new ExpenseAdapter( getActivity(), R.layout.expense_item, expenses );
        mExpenseListView.setAdapter( mExpenseAdapter );

        // Initialize progress bar
        mProgressBar.setVisibility( ProgressBar.VISIBLE );


        //Add new expense
        mAddExpenseFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to Add new expense activity
                Intent i = new Intent( getActivity(), AddExpensesActivity.class );
                startActivity( i );

            }
        } );

mExpenseListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        //return the object in the list View
        Expense expense = expenses.get( pos );
        Intent in = new Intent( getActivity(), ViewExpensesActivity.class );
        //pass the expense number to the next activity
        in.putExtra( "expenseNum", expense.getExpenseNum() );
        startActivity( in );
        Log.v(TAG, expense.toString());
    }
} );
        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (user == null) {

                    // User is signed out
                    //go to login activity
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    toastMessage("Successfully signed out");
                }
            }
        };

        //Attach the onChildAdded listener only when the activity created
        //no duplicates
        attachDatabaseReadListener();
        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, TAG + " is resumed");
        // attachDatabaseReadListener();
    }



    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        Log.v(TAG, TAG + " on pause");
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {

            mExpenseDatabaseReference.removeEventListener( mChildEventListener );
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Expense expense = dataSnapshot.getValue( Expense.class );
                    mExpenseAdapter.add( expense );
                    mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    if (!dataSnapshot.exists()) {
                        mProgressBar.setVisibility( ProgressBar.INVISIBLE );
                    }
                    Log.d( TAG + "Added", dataSnapshot.getValue( Expense.class ).toString() );
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mExpenseDatabaseReference.addChildEventListener( mChildEventListener );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle( R.string.expenses_text );
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }


}
