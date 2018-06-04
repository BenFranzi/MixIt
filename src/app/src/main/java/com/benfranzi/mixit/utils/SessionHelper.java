/*
 * Copyright 2018 Benjamin Franzi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.benfranzi.mixit.utils;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Session helper that provides functionality to handle account related functions globally.
 */
public class SessionHelper {
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;
    private static SessionHelper mInstance;

    /**
     * Follows the singleton pattern, gets an instance of the Session Helper.
     */
    public synchronized static SessionHelper get(Context context, boolean restart) {
        if (mInstance == null) { //Create helper on first get
            mInstance = new SessionHelper(context.getApplicationContext()); //app context is at the highest scope
        } else if (restart) {
            mInstance = new SessionHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Constructor that configures the google sign in api.
     */
    private SessionHelper(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Called to sign out of the application.
     */
    public void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        returnToSignIn();
    }

    /**
     * Returns the user to the main menu and prevents the back button from
     * returning them to the previous menu.
     */
    public void returnToSignIn() {
        Intent i = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        mContext.getApplicationContext().startActivity(i);
    }
}
