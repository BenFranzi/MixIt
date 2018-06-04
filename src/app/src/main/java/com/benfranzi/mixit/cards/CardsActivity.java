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

package com.benfranzi.mixit.cards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.base.BaseActivity;
import com.benfranzi.mixit.utils.Helpers;
import com.benfranzi.mixit.utils.Logs;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * View for the CardsActivity. Responsible for presenting data, separated from the model by
 * the presenter.
 */
public class CardsActivity extends BaseActivity<CardsPresenter>
        implements CardsContract.View, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.cards_msg_tv)
    TextView mMsgTv;
    @BindView(R.id.cards_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.cards_stack_view)
    CardStackView mCardStack;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    private boolean mSignedIn;

    /**
     * Returns layout to be inflated.
     */
    @Override
    protected int getContentResource() {
        return R.layout.activity_cards;
    }

    /**
     * Initial setup, called on activity create.
     */
    @Override
    protected void init(@Nullable Bundle state) {
        mSignedIn = getPresenter().loadUI();
        //If there is a user logged in run the initial configuration.
        if (mSignedIn) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            mNavigationView.setNavigationItemSelectedListener(this);
        }
    }

    /**
     * Injects dependencies.
     */
    @Override
    public void injectDependencies() {
        getActivityComponent().injectCards(this);
    }

    /**
     * Disconnects the "shake" detection if activity is left.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mSignedIn) {
            getPresenter().disconnectShake();
        }
    }

    /**
     * Reconnects the "shake" detection if activity is entered.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mSignedIn) {
            getPresenter().reconnectShake();
        }
    }

    /**
     * Returns the context of the view.
     */
    @Override
    public Context getContext() {
        return CardsActivity.this;
    }

    /**
     * Sets up the CardAdapter by attaching the cardAdapter class.
     */
    @Override
    public void setCardAdapter(CardAdapter cardAdapter) {
        mCardStack.setAdapter(cardAdapter);
    }

    /**
     * Toggles the progress bar on loading and if no cards are loaded, provides an error message.
     */
    @Override
    public void toggleProgressBar(boolean visible, boolean failedFilter) {
        if (visible) {
            mCardStack.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mMsgTv.setVisibility(View.GONE);
        } else {
            mCardStack.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mMsgTv.setVisibility(View.VISIBLE);
            if (failedFilter) {
                mMsgTv.setText(R.string.filter_failed);
            } else {
                mMsgTv.setText(R.string.reached_end);
            }
        }

    }

    /**
     * Initial configuration for the card stack.
     */
    @Override
    public void setupCards() {
        toggleProgressBar(true, false);

        mCardStack.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {
            }
            @Override
            public void onCardSwiped(SwipeDirection direction) {
                if (direction == SwipeDirection.Top) {
                    getPresenter().handleAddFavourite(mCardStack.getTopIndex() - 1);
                }
            }
            @Override
            public void onCardReversed() {
            }
            @Override
            public void onCardMovedToOrigin() {
            }
            @Override
            public void onCardClicked(int index) {
                getPresenter().openCard(index);
            }
        });
    }

    /**
     * Closes drawer if open on back press.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Inflate the menu, adds items to the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cards, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. If signout is selected, the user
     * is signed out and returned to the menu. If suggest drink is selected,
     * the user receives a notification with a recommendation.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_signout:
                getPresenter().signOut();
                finish();
                return true;
            case R.id.action_suggest:
                getPresenter().suggestDrink();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles navigation view item clicks. If random is selected, the card
     * stack is re-shuffled and loaded. If favourites is selected, the
     * favourite Activity is launched. Any other click toggles the filter
     * state in the presenter.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_random:
                getPresenter().getRandom();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_favourites:
                getPresenter().startFavouriteActivity();
                break;
            default:
                getPresenter().filterStateChange(id);
                break;
        }
        return false;
    }

    /**
     * Updates the navigation view filter list to reflect the filter state.
     */
    @Override
    public void updateFilterMenu(boolean[] items) {
        mNavigationView.getMenu().findItem(R.id.nav_item_vodka).setChecked(items[0]);
        mNavigationView.getMenu().findItem(R.id.nav_item_gin).setChecked(items[1]);
        mNavigationView.getMenu().findItem(R.id.nav_item_whiskey).setChecked(items[2]);
        mNavigationView.getMenu().findItem(R.id.nav_item_rum).setChecked(items[3]);
        mNavigationView.getMenu().findItem(R.id.nav_item_tequila).setChecked(items[4]);
    }

    /**
     * Makes a snackbar message. Used to provide error messages.
     */
    @Override
    public void makeSnackbar(String msg) {
        Helpers.makeSnackbar(findViewById(R.id.activity_drink_id), msg);
    }
}
