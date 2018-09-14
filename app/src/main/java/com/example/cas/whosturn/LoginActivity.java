package com.example.cas.whosturn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GoogleSignIn";
    private static final int RC_SIGN_IN = 9001;
    //declare instance of FirebaseAuth
    private FirebaseAuth mAuth;

    //Google Sign In Client
    GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mTitleMessageTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mTitleMessageTextView=findViewById(R.id.title_message);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        //initialize FirebaseAuth instance
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this,googleSignInOptions);
        mAuth=FirebaseAuth.getInstance();
    }//onCreate() END

    //onStart
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }//onStart() END

    //onActivityResult()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //firbaseAuthentication using google credentials
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }//onActivityResult() END

    //firebase Authentication with Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user=mAuth.getCurrentUser();
                            updateUI(user);
                        }else{
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }//firebase auth with google END

    //signIn() method
    private void signIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }//signIn() END

    //signOut() method
    private void signOut(){
        //Firebase sign out
        mAuth.signOut();

        //Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }//signOut() END

    //revokeAccess() method
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }//revokeAccess() END

    private void updateUI(FirebaseUser currentLoggedUser){
        hideProgressDialog();
        if(currentLoggedUser!=null){
            mStatusTextView.setText(getString(R.string.google_status_fmt,currentLoggedUser.getDisplayName()));
            mTitleMessageTextView.setText(R.string.title_message_logged_in);

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        }else{
            mStatusTextView.setText("Signed Out");
            mTitleMessageTextView.setText(R.string.title_message_logged_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }



    //onClick implemented here
    @Override
    public void onClick(View v) {
        int i=v.getId();
        if(i==R.id.sign_in_button){
            signIn();
        }else if(i==R.id.sign_out_button){
            signOut();
        }else if(i==R.id.disconnect_button){
            revokeAccess();
        }

    }
}
