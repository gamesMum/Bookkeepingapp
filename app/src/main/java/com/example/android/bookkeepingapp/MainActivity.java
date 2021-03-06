package com.example.android.bookkeepingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private TextView mSignout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle  bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.getString( "fragmentName" ) != null)
            {
            switch (bundle.getString( "fragmentName" )) {
                case "summeryFragment":
                    getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new SummeryFragment() ).commit();

                    break;
                case "clientFragment":
                    getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new ClientFragment() ).commit();
                    break;

                case "invoiceFragment":
                    getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new InvoicesFragment() ).commit();
                    break;

                case "serviceFragment":
                    //go to service fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceFragment()).commit();

                    break;

                case "expenseFragment":
                    //go to service fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ExpensesFragment()).commit();

                    break;


                default:
                        getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new SummeryFragment() ).commit();
                        break;

            }
            }

        }

            //find the TextView with Id signout_text
        mSignout = (TextView) findViewById(R.id.signout_text);

        //// Set a Toolbar to replace the ActionBar.
       toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        ActionBar actionbar = getSupportActionBar();
        //Enable the app bar's "home" button
        actionbar.setDisplayHomeAsUpEnabled(true);
        //change it to use the edit_menu icon
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //find the DrawerLayout
        //if I need to implement DrawerListener to the drawer
        //onDrawerSlide, onDrawerOpened, onDrawerClosed, onDrawerStateChanged
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        //find the navigationView (contains the drawer items)
        //and implement the interface OnNavigationItemSelectedListener
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                displayFragment(menuItem);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();


                return true;
            }
        });

        //Check user if authenticated
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    //go to login activity
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    //toastMessage("Successfully signed out");
                }
                else
                {
                    //go to summery fragment
                   // getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SummeryFragment()).commit();
                    toolbar.setTitle( R.string.summery_text );
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SummeryFragment fragment = new SummeryFragment();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.commit();
                }
            }
        };

        //attach click listener to signout_text
        mSignout.setOnClickListener(new View.OnClickListener() {
            //Signout
            @Override
            public void onClick(View v) {
              mAuth.signOut();
              toastMessage("Signing Out...");
                //go to login activity
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });


    }



    private void displayFragment(MenuItem item)
    {
        // Add code here to update the UI based on the item selected
        // For example, swap UI fragments here

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(item.getItemId()) {
            case R.id.nav_customers:
                fragmentClass = ClientFragment.class;

                break;
            case R.id.nav_expendings:
                fragmentClass = ExpensesFragment.class;
                break;
            case R.id.nav_invoices:
                fragmentClass = InvoicesFragment.class;
                break;
            case R.id.nav_summery:
                fragmentClass = SummeryFragment.class;
                break;
            case R.id.nav_services:
                fragmentClass = ServiceFragment.class;
                break;
            default:
                fragmentClass = SummeryFragment.class;
        }
        //Create new Instance of the fragmentClass (ClientFragment, ExpendingFragment,...)
        try
        {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // set item as selected to persist highlight
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }

    //To open the drawer when the user taps on the nav drawer button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,  R.string.drawer_close);
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
        {
            //go to summery fragment
            // getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SummeryFragment()).commit();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SummeryFragment fragment = new SummeryFragment();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            toolbar.setTitle( R.string.summery_text );
            fragmentTransaction.commit();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        String  extras = getIntent().getStringExtra("fragmentName");
        if (extras != null) {
            switch (extras) {
                case "summeryFragment":
                    getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new SummeryFragment() ).commit();
                    break;
                case "clientFragment":
                    getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new ClientFragment() ).commit();
                    break;

                case "invoiceFragment":
                    getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new InvoicesFragment() ).commit();
                    break;

                case "serviceFragment":
                    getSupportFragmentManager().beginTransaction().replace( R.id.content_frame, new ServiceFragment() ).commit();
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }



}
