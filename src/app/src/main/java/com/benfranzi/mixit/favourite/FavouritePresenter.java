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

package com.benfranzi.mixit.favourite;

import com.benfranzi.mixit.base.BasePresenter;
import com.benfranzi.mixit.model.Drink;
import com.benfranzi.mixit.utils.FirebaseHelper;
import com.benfranzi.mixit.utils.Helpers;
import com.benfranzi.mixit.utils.Logs;
import com.benfranzi.mixit.utils.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Presenter for the FavouriteActivity. Provides "middle-man" functionality between the model and the view.
 */
public class FavouritePresenter extends BasePresenter<FavouriteContract.View> implements FavouriteContract.Presenter {

    private FavouriteAdapter mFavouriteAdapter;
    private ArrayList<Drink> mDrinks;
    private ArrayList<Drink> mFavourites;

    /**
     * Inject components.
     */
    @Inject
    public FavouritePresenter() {
    }

    /**
     * Called on start. Calls the getDrinks function to load the favourites list.
     */
    @Override
    public void onLoadActivity() {
        getDrinks();
    }

    /**
     * Initialises the view from the presenter.
     */
    @Override
    public void updateUI() {
        mFavouriteAdapter = new FavouriteAdapter(getView().getContext(), mFavourites);
        getView().setupRecyclerView(mFavouriteAdapter);
        mFavouriteAdapter.notifyDataSetChanged();
    }

    /**
     * Gets the drinks list and makes a call to get the favourites list.
     */
    private void getDrinks() {
        FirebaseHelper.get().getDrinks(new OnGetDataListener() {
            @Override
            public void onStart() {
                getView().toggleProgressBar(true);
            }
            @Override
            public void onSuccess(DataSnapshot data) {
                mDrinks = Helpers.DrinksToPOJO(data);
                getFavourites();
            }
            @Override
            public void onFailed(DatabaseError databaseError) {
                getView().toggleProgressBar(false);
                Logs.logv("Failed to get drinks due to database error");
            }
        });
    }

    /**
     * Gets the drinks list and compares it to the favourites list then finally updates the ui.
     */
    private void getFavourites() {
        FirebaseHelper.get().getFavourites(new OnGetDataListener() {
            @Override
            public void onStart() {}
            @Override
            public void onSuccess(DataSnapshot data) {
                mFavourites = Helpers.getDrinksFromFavourites(mDrinks, Helpers.getFavourites(data));
                if (getView() != null) {
                    getView().toggleProgressBar(false);
                }
                updateUI();
            }
            @Override
            public void onFailed(DatabaseError databaseError) {
                Logs.logv("Failed to get drinks due to database error");
            }
        });

    }
}

