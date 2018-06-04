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

package com.benfranzi.mixit.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.base.BasePresenter;
import com.benfranzi.mixit.utils.Logs;
import com.benfranzi.mixit.cards.CardsActivity;
import com.benfranzi.mixit.utils.SessionHelper;
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
import javax.inject.Inject;

/**
 * Presenter for the LoginActivity. Provides "middle-man" functionality between the model and the view.
 */
public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private static final String GOOGLE_USER = "google_user";
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Inject components.
     */
    @Inject
    public LoginPresenter() {
    }

    /**
     * Sets up the sign in option.
     */
    @Override
    public void configureSigninOptions() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getView().getActivityContext().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getView().getActivityContext(), gso);
    }

    /**
     * Called by the Google sign in button. Starts the Google sign in process.
     */
    @Override
    public void loginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        getView().startActForRes(signInIntent, RC_SIGN_IN);
    }

    /**
     * Checks for existing user already signed in. If a user is signed in, it updates the UI.
     */
    @Override
    public void onStartPresenter() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    /**
     * Result for the sign in.
     */
    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Logs.logv("Google sign in failed");
                getView().makeSnackbar("Google sign in failed.");
                updateUI(null);
            }
        }
    }

    /**
     * Changes the UI based on sign in result.
     */
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            SessionHelper.get(getView().getActivityContext(),true);
            Intent i = new Intent(getView().getActivityContext(), CardsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            getView().getActivityContext().startActivity(i);
        }
    }

    /**
     * Signs in with Google using FirebaseAuth
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        getView().showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Logs.logv("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            getView().hideProgressDialog();
                        } else {
                            // If sign in fails, display a message to the user.
                            Logs.logv("signInWithCredential:failure: "+ task.getException());
                            getView().hideProgressDialog();
                            getView().makeSnackbar("Authentication Failed.");
                            updateUI(null);
                        }}
                });
    }
}
