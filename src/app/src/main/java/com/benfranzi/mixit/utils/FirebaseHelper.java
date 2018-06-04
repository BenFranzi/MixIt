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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Provides firebase helper functions for the application.
 */
public class FirebaseHelper {

    public static final String COCKTAILS_KEY = "cocktails";
    public static final String USERS_KEY = "users";
    FirebaseDatabase mDatabase;
    FirebaseUser mUser;
    private static FirebaseHelper mInstance;

    /**
     * Follows the singleton pattern, gets an instance of the Firebase Helper.
     */
    public synchronized static FirebaseHelper get() {
        if (mInstance == null) { //Create helper on first get
            mInstance = new FirebaseHelper(); //app context is at the highest scope
        }
        return mInstance;
    }

    /**
     * Sets up the firebase database instance.
     */
    FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    /**
     * Gets the drinks list as a data snapshot. To be overriden where needed.
     */
    public void getDrinks(final OnGetDataListener listener) {
        listener.onStart();
        mDatabase.getReference().child(COCKTAILS_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * Gets the favourites list as a data snapshot. To be overriden where needed.
     */
    public void getFavourites(final OnGetDataListener listener) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        listener.onStart();
        mDatabase.getReference(USERS_KEY).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * Add a favourite to the user's favourites list.
     */
    public void addFavourite(String favourite) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.getReference(USERS_KEY).child(mUser.getUid()).child(Helpers.hash(favourite)).setValue(true);
    }

    /**
     * Removes a favourite from the user's favourites list.
     */
    public void deleteFavourite(String favourite) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.getReference(USERS_KEY).child(mUser.getUid()).child(Helpers.hash(favourite)).setValue(false);
    }
 }
