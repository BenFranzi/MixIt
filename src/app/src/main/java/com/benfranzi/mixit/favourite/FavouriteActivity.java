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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.base.BaseActivity;
import com.benfranzi.mixit.model.Drink;
import butterknife.BindView;
import static com.benfranzi.mixit.cards.CardsPresenter.DRINK;

/**
 * View for the FavouriteActivity. Responsible for presenting data, separated from the model by
 * the presenter.
 */
public class FavouriteActivity extends BaseActivity<FavouritePresenter> implements FavouriteContract.View {

    @BindView(R.id.favourite_recyclerview_rv)
    RecyclerView mFavouriteRv;
    @BindView(R.id.favourite_progress_bar)
    ProgressBar mProgressBar;

    /**
     * Initial setup, called on activity create.
     */
    @Override
    protected void init(@Nullable Bundle state) {
        Intent i = getIntent();
        Drink mDrink = (Drink) i.getSerializableExtra(DRINK);
        getPresenter().onLoadActivity();
    }

    /**
     * Gets the favourites list.
     */
    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onLoadActivity();
    }

    /**
     * Returns layout to be inflated.
     */
    @Override
    protected int getContentResource() {
        return R.layout.activity_favourite;
    }

    /**
     * Returns the context.
     */
    public Context getContext() {
        return FavouriteActivity.this;
    }

    /**
     * Injects dependencies.
     */
    @Override
    public void injectDependencies() {
        getActivityComponent().injectFavourite(this);
    }

    /**
     * Configures the recyclerview based on the presenter.
     */
    @Override
    public void setupRecyclerView(FavouriteAdapter favouriteAdapter) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mFavouriteRv.setLayoutManager(mLayoutManager);
        mFavouriteRv.setItemAnimator(new DefaultItemAnimator());
        mFavouriteRv.setAdapter(favouriteAdapter);
    }

    /**
     * Toggles the progress bar on loading.
     */
    @Override
    public void toggleProgressBar(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(View.VISIBLE);
            mFavouriteRv.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mFavouriteRv.setVisibility(View.VISIBLE);
        }
    }
}
