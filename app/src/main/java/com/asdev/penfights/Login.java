package com.asdev.penfights;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.asdev.penfights.helper.check;
import com.asdev.penfights.helper.CustomToast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    //User Interface elements declaration
    private Button googleLoginBtn;


    // Authentication elements declaration
    private FirebaseAuth mAuth;
    private GoogleSignInClient signInClient;
    private final static int RC_SIGN_IN = 1;
    private  GoogleSignInAccount account;
    private String USER_ID;


    //Database elements declaration
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference userDB = db.getReference().child("user").getRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleLoginBtn = findViewById(R.id.google_login_btn);

        mAuth = FirebaseAuth.getInstance();

        // Configuring Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this,gso);


        //On Click Listeners
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isGoogleAccountChangeCall())
        {
                signInClient.signOut();
        }
    }

    private boolean isGoogleAccountChangeCall() {

        int CALL_FLAG =getIntent().getIntExtra("CALL_FLAG",0);
        if(CALL_FLAG == new check().GOOGLE_ACCOUNT_CHANGE_CALL_FLAG )
        {
            return true;
        }
        else
            return false;



    }

    public void signin()  // Initialises the signin process
    {
        Intent signinIntent = signInClient.getSignInIntent();
        startActivityForResult(signinIntent,RC_SIGN_IN);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase, check if he user exists in the database
                account = task.getResult(ApiException.class);
                USER_ID = account.getId();
                loginUser();

            } catch (ApiException e) {
                showToast("Oops! Unable to login");
            }
        }
    }

    // Sends user to registration screen bundling the google account
    private void registeruserAction(GoogleSignInAccount account) {

        Intent registerUserIntent = new Intent(Login.this, Register.class);
        registerUserIntent.putExtra("GOOGLE_ACCOUNT", account);
        startActivity(registerUserIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }


    //Authenticates the user to firebase.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        }
                        else
                        {
                            showToast("Oops! Unable to connect to server");
                        }

                        // ...
                    }
                });

    }

        // Checks if the user exists in the database [ YES -> Login ] [ NO -> Register ]
        private void loginUser()
         {

            userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(USER_ID))
                    {
                        firebaseAuthWithGoogle(account); // Authenticate the user with firebase
                    }
                    else
                    {
                        registeruserAction(account); // Take user to registration screen
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast("Problem in connection to server.");
                }
            });
        }

    private void showToast(String MESSAGE) {

        CustomToast toast = new CustomToast(this);
        toast.setMessage(MESSAGE);
        toast.setLongDuration();
        toast.show();
    }
}
