package com.benfranzi.mixit;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    public final static String MAIN_LOG         = "main_log";
    public final static int RC_SIGN_IN_EMAIL    = 0; //Email
    public final static int RC_SIGN_IN_GOOGLE   = 1; //Google
    public final static int RC_SIGN_IN_FACEBOOK = 2; //Facebook
    public final static int RC_SIGN_IN_TWITTER  = 3; //Twitter
    private LinearLayout mBackgroundLl;
    private AnimationDrawable mBackgroundAd;

    private Button mEmailSigninBt;
    private Button mGoogleSigninBt;
    private Button mFacebookSigninBt;
    private Button mTwitterSigninBt;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // [START initialise_members]
        mBackgroundLl = (LinearLayout) findViewById(R.id.main_background_ll);

        mEmailSigninBt = (Button) findViewById(R.id.main_email_si_bt);
        mGoogleSigninBt = (Button) findViewById(R.id.main_google_si_bt);
        mFacebookSigninBt = (Button) findViewById(R.id.main_facebook_si_bt);
        mTwitterSigninBt = (Button) findViewById(R.id.main_twitter_si_bt);

        mAuth = FirebaseAuth.getInstance();
        // [END initialise_members]

        // [START animate_background]
        mBackgroundAd = (AnimationDrawable) mBackgroundLl.getBackground();
        mBackgroundAd.setEnterFadeDuration(6000);
        mBackgroundAd.setExitFadeDuration(2000);
        // [END animate_background]




        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSigninBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Background Animation
        if (mBackgroundAd != null && !mBackgroundAd.isRunning())
            mBackgroundAd.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Background Animation
        if (mBackgroundAd != null && mBackgroundAd.isRunning())
            mBackgroundAd.stop();
    }

    void updateUI(FirebaseUser currentUser) {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseAuthWithGoogle(task.getResult());
        }
    }

    void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(MAIN_LOG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(MAIN_LOG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(MAIN_LOG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }}
                });
    }

}


