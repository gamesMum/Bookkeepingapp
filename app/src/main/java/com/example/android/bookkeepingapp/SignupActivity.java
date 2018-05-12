package com.example.android.bookkeepingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity" ;
    private Button btnSignUp,btnLinkToLogIn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private EditText edSignupInputEmail, edSignupInputPassword;
    private TextInputLayout signupInputLayoutEmail, signupInputLayoutPassword;

    private String newUserID;
    private String newUserEmail;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        signupInputLayoutEmail = (TextInputLayout) findViewById(R.id.signup_input_layout_email);
        signupInputLayoutPassword = (TextInputLayout) findViewById(R.id.signup_input_layout_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        edSignupInputEmail = (EditText) findViewById(R.id.ed_SignUP_Input_Email);
        edSignupInputPassword = (EditText) findViewById(R.id.ed_SignUP_Input_Password);

        btnSignUp = (Button) findViewById(R.id.btn_SignUp);
        btnLinkToLogIn = (Button) findViewById(R.id.btn_Link_Login);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();

            }
        });

        btnLinkToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Validating form
     */
    private void submitForm() {

        String email = edSignupInputEmail.getText().toString().trim();
        String password = edSignupInputPassword.getText().toString().trim();

        if(!checkEmail()) {
            return;
        }
        if(!checkPassword()) {
            return;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        signupInputLayoutPassword.setErrorEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"createUserWithEmail:onComplete:" + task.isSuccessful());
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, Log the message to the LogCat. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d(TAG,"Authentication failed." + task.getException());

                        } else {
                            //Check if the user logged in for the first time
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew) {
                                newUserID = task.getResult().getUser().getUid();
                                newUserEmail = task.getResult().getUser().getEmail();
                                //store the data under loggedin user Id
                                mUserDatabaseReference = mFirebaseDatabase.getReference().child( newUserID ).child( "user" );

                                addUserInfo( newUserID, newUserEmail );
                                 
                                Log.d( TAG, "onComplete: " + (isNew ? "new user" : "old user") );
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            }
                            else //if old user
                                 {
                                startActivity( new Intent( SignupActivity.this, MainActivity.class ) );
                                finish();
                            }
                        }
                    }
                });
        Toast.makeText(getApplicationContext(), "You are successfully Registered !!", Toast.LENGTH_SHORT).show();
    }

    private void addUserInfo(String userId, String userEmail) {
        User user = new User( userId, newUserEmail, 0);
        mUserDatabaseReference.setValue( user );

    }

    private boolean checkEmail() {
        String email = edSignupInputEmail.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {

            signupInputLayoutEmail.setErrorEnabled(true);
            signupInputLayoutEmail.setError(getString(R.string.err_msg_email));
            edSignupInputEmail.setError(getString(R.string.err_msg_required));
            requestFocus(edSignupInputEmail);
            return false;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {

        String password = edSignupInputPassword.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {

            signupInputLayoutPassword.setError(getString(R.string.err_msg_password));
            edSignupInputPassword.setError(getString(R.string.err_msg_required));
            requestFocus(edSignupInputPassword);
            return false;
        }
        signupInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isPasswordValid(String password){
        return (password.length() >= 6);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
