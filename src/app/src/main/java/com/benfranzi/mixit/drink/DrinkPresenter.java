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

package com.benfranzi.mixit.drink;

import android.content.Intent;

import com.benfranzi.mixit.base.BasePresenter;
import com.benfranzi.mixit.model.Drink;
import com.benfranzi.mixit.utils.FirebaseHelper;
import com.benfranzi.mixit.utils.Helpers;
import com.benfranzi.mixit.utils.Logs;
import com.benfranzi.mixit.utils.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.Map;
import javax.inject.Inject;
import static com.benfranzi.mixit.cards.CardsPresenter.DRINK;

/**
 * Presenter for the DrinkActivity. Provides "middle-man" functionality between the model and the view.
 */
public class DrinkPresenter extends BasePresenter<DrinkContract.View> implements DrinkContract.Presenter  {

    Drink mDrink;
    Intent mIntent;

    /**
     * Inject components.
     */
    @Inject
    public DrinkPresenter() {
    }

    /**
     * Toggles the drink favourite status.
     */
    private void toggleFavorite(boolean favourite) {
        //if already favourite
        if (favourite) {
            //remove favourite
            FirebaseHelper.get().deleteFavourite(mDrink.getName());
        } else {
            //add favourite
            FirebaseHelper.get().addFavourite(mDrink.getName());
        }
    }

    /**
     * Gets the intents and loads the selected drink model.
     */
    @Override
    public void initialLoad(Intent i) {
        mIntent = i;
        mDrink = (Drink) i.getSerializableExtra(DRINK);
        changeFavourite(false);
    }

    /**
     * Gets the favourites and toggles the selected favourite item.
     */
    @Override
    public void changeFavourite(final boolean toggle) {
        FirebaseHelper.get().getFavourites(new OnGetDataListener() {
            @Override
            public void onStart() {
                getView().toggleProgressBar(true);
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                Map<String, Boolean> dataMap = (Map<String, Boolean>) data.getValue();

                boolean isFavourite = false;
                if (dataMap != null) {
                    if (dataMap.get(Helpers.hash(mDrink.getName())) != null)
                        if (dataMap.get(Helpers.hash(mDrink.getName())) == true) {
                            isFavourite = true;
                        }
                }
                if (toggle) {
                    toggleFavorite(isFavourite);
                    loadUI(!isFavourite);
                } else  {
                    loadUI(isFavourite);
                }
                if (getView() != null) {
                    getView().toggleProgressBar(false);
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Logs.logv("Failed to get drinks due to database error");
            }
        });

    }
    /**
     * Loads the data from the model into the view.
     */
    @Override
    public void loadUI(boolean favourite) {
        if (getView() != null) {
            getView().updateUI(mDrink.getName(),
                    mDrink.getGlass(),
                    mDrink.getCategory(),
                    mDrink.getInstructions(),
                    mDrink.getMeasures(),
                    mDrink.getIngredients(),
                    favourite);
        }
    }
}
