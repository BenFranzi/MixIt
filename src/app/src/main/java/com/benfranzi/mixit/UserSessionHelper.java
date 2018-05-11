package com.benfranzi.mixit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static com.benfranzi.mixit.MainActivity.RC_SIGN_IN_GOOGLE;

/**
 * Created by benfranzi on 11/5/18.
 */

public class UserSessionHelper {

    public static final String USER_SESSION_HELPER_LOG = "user_session_helper_log";

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private GoogleApiClient mGoogleApiClient;

    private static UserSessionHelper mInstance;


    public synchronized static UserSessionHelper get(Context context) {
        if (mInstance == null) { //Create helper on first get
            mInstance = new UserSessionHelper(context.getApplicationContext()); //app context is at the highest scope
        }
        return mInstance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseUser getUser() {
        return mUser;
    }

    private UserSessionHelper(Context context) {
        mContext = context;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        List<String> providers = mUser.getProviders();
        switch (providers.get(0)) {
            case "google.com":
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                mGoogleApiClient.connect();
                break;
            default:
                Log.d(USER_SESSION_HELPER_LOG, "no provider, the app will crash on sign out");
                break;
        }
    }

    public void signOut() {
        mAuth.signOut();
        if (mGoogleApiClient != null) {
            signoutGoogleUser();
        }
        finishSignOut();
    }

    private void signoutGoogleUser() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    private void finishSignOut() {
        Intent i = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        mContext.getApplicationContext().startActivity(i);
    }
}
