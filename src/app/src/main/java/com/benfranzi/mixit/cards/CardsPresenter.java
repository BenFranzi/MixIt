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
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.base.BasePresenter;
import com.benfranzi.mixit.drink.DrinkActivity;
import com.benfranzi.mixit.favourite.FavouriteActivity;
import com.benfranzi.mixit.utils.DrinksNotificationsManager;
import com.benfranzi.mixit.utils.Logs;
import com.benfranzi.mixit.model.Drink;
import com.benfranzi.mixit.utils.FirebaseHelper;
import com.benfranzi.mixit.utils.Helpers;
import com.benfranzi.mixit.utils.OnGetDataListener;
import com.benfranzi.mixit.utils.SessionHelper;
import com.benfranzi.mixit.utils.ShakeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.inject.Inject;

/**
 * Presenter for the CardsActivity. Provides "middle-man" functionality between the model and the view.
 */
public class CardsPresenter extends BasePresenter<CardsContract.View> implements CardsContract.Presenter {

    public static final String DRINK = "drink";
    public static final int RANDOM_AMOUNT = 10;
    public static final int FILTER_SIZE = 5;
    private boolean[] mFilterState = new boolean[FILTER_SIZE];
    CardAdapter mCardAdapter;
    ArrayList<Drink> mDrinks;
    FirebaseUser mUser;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    /**
     * Inject components.
     */
    @Inject
    public CardsPresenter() {
    }

    /**
     * Signs user out of the application using the session helper class.
     */
    @Override
    public void signOut() {
        SessionHelper.get(getView().getContext(), false).signOut();
    }

    /**
     * Called on start. Initialises the view from the presenter.
     */
    @Override
    public boolean loadUI() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            getView().setupCards();
            getDrinks();
            Arrays.fill(mFilterState, Boolean.FALSE);
            setupShake();
            return true;
        } else {
            SessionHelper.get(getView().getContext(), true).returnToSignIn();
            return false;
        }
    }

    /**
     * Toggles the search filter based on selected item in the drawer.
     */
    @Override
    public void filterStateChange(int item) {
        switch (item) {
            case R.id.nav_item_vodka:
                mFilterState[0] = !mFilterState[0];
                break;
            case R.id.nav_item_gin:
                mFilterState[1] = !mFilterState[1];
                break;
            case R.id.nav_item_whiskey:
                mFilterState[2] = !mFilterState[2];
                break;
            case R.id.nav_item_rum:
                mFilterState[3] = !mFilterState[3];
                break;
            case R.id.nav_item_tequila:
                mFilterState[4] = !mFilterState[4];
                break;
        }
        getView().updateFilterMenu(mFilterState);
    }

    /**
     * Called when random is selected in the drawer. Publicly accessible function to get drinks for the card view.
     */
    @Override
    public void getRandom() {
        getDrinks();
    }

    /**
     * Called when favourite is selected in the drawer. Launches the Favourite Activity.
     */
    @Override
    public void startFavouriteActivity() {
        Intent i = new Intent(getView().getContext(), FavouriteActivity.class);
        getView().getContext().startActivity(i);
    }

    /**
     * Called when a card is selected. Opens the drink inside of the Drink Activity.
     */
    @Override
    public void openCard(int index) {
        Drink drink = mDrinks.get(index);
        Intent i = new Intent(getView().getContext(), DrinkActivity.class);
        i.putExtra(DRINK, drink);
        getView().getContext().startActivity(i);
    }

    /**
     * Creates the card adapter on launch if drinks is not null.
     */
    private CardAdapter createCardAdapter(ArrayList<Drink> drinks) {
        final CardAdapter adapter;
        if (getView() != null) {
                adapter = new CardAdapter(getView().getContext());
            if (drinks != null) {
                adapter.addAll(drinks);
            }
        } else {
            adapter = new CardAdapter(null);
        }
        return adapter;
    }

    /**
     * Called when a favourite is added by swiping up on a card.
     */
    @Override
    public void handleAddFavourite(int index) {
        FirebaseHelper.get().addFavourite(mDrinks.get(index).getName());
    }

    /**
     * When the view is out of focus the "shake" detection is disabled.
     */
    @Override
    public void disconnectShake() {
        mSensorManager.unregisterListener(mShakeDetector);
    }

    /**
     * When the view is in focus the "shake" detection is enabled.
     */
    @Override
    public void reconnectShake() {
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Configures the "shake" detection.
     */
    private void setupShake() {
        mSensorManager = (SensorManager) getView().getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                Vibrator v = (Vibrator) getView().getContext().getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                getRandom();
            }
        });
    }

    /**
     * Sends a notification with a drink suggestion to the user upon request.
     */
    @Override
    public void suggestDrink() {
        if (mDrinks != null) {
            if (mDrinks.size() != 0) {
                Random rnd = new Random();
                Drink drink = mDrinks.get(rnd.nextInt(RANDOM_AMOUNT));
                DrinksNotificationsManager.get(getView().getContext()).make(drink);
            }
        }
    }

    /**
     * Gets lists of drinks and filters based on preferences.
     */
    private void getDrinks() {
        FirebaseHelper.get().getDrinks(new OnGetDataListener() {
            @Override
            public void onStart() {
                getView().toggleProgressBar(true, false);
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                mDrinks = Helpers.DrinksToPOJO(data);
                mDrinks = Helpers.pickRandom(RANDOM_AMOUNT, mDrinks, mFilterState);
                mCardAdapter = createCardAdapter(mDrinks);
                if (mCardAdapter != null) {
                    getView().setCardAdapter(mCardAdapter);
                }
                getView().toggleProgressBar(false, (mDrinks == null));
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                getView().toggleProgressBar(false, false);
                Logs.logv("Failed to get drinks due to database error");
            }
        });
    }
}