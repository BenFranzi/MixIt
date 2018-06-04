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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.base.BaseActivity;
import com.benfranzi.mixit.utils.Helpers;
import com.google.android.gms.common.SignInButton;
import butterknife.BindView;

/**
 * View for the LoginActivity. Responsible for presenting data, separated from the model by
 * the presenter.
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.login_google_lbt)
    SignInButton mGoogleLbt;
    @BindView(R.id.main_background_ll)
    LinearLayout mBackgroundLl;
    private AnimationDrawable mBackgroundAd;
    private ProgressDialog mProgressDialog;

    /**
     * Returns layout to be inflated.
     */
    @Override
    protected int getContentResource() {
        return R.layout.activity_login;
    }

    /**
     * Initial setup, called on activity create. Sets up the background animation and assigns the
     * Google sigin in onclick to a function in the presenter.
     */
    @Override
    protected void init(@Nullable Bundle state) {
        getPresenter().configureSigninOptions();
        mGoogleLbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().loginGoogle();
            }
        });
        mBackgroundAd = (AnimationDrawable) mBackgroundLl.getBackground();
        mBackgroundAd.setEnterFadeDuration(6000);
        mBackgroundAd.setExitFadeDuration(2000);
    }

    /**
     * Injects dependencies.
     */
    @Override
    public void injectDependencies() {
        getActivityComponent().injectLogin(this);
    }

    /**
     * Returns the views context.
     */
    @Override
    public Context getActivityContext() {
        return getApplicationContext();
    }

    /**
     * Makes a snackbar message. Used to provide error messages.
     */
    @Override
    public void makeSnackbar(String msg) {
        Helpers.makeSnackbar(findViewById(R.id.activity_login_id), msg);
    }

    /**
     * Starts the sign in process, called by the google sign in button click.
     */
    @Override
    public void startActForRes(Intent i, int code) {
        startActivityForResult(i, code);
    }

    /**
     * Processes sign in and updates the application accordingly
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().result(requestCode, resultCode, data);
    }

    /**
     * Toggles the progress dialog to be visible on loading.
     */
    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    /**
     * Toggles the progress dialog to be hidden after loading.
     */
    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Calls the start function to check for existing user signed in.
     */
    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onStartPresenter();
    }

    /**
     * Starts the background animation on resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mBackgroundAd != null && !mBackgroundAd.isRunning()) {
            mBackgroundAd.start();
        }
    }

    /**
     * Pauses the background animation on stop.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mBackgroundAd != null && mBackgroundAd.isRunning()) {
            mBackgroundAd.stop();
        }
    }
}
