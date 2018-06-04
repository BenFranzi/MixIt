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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.base.BaseActivity;
import com.benfranzi.mixit.model.Drink;
import com.benfranzi.mixit.utils.Helpers;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import butterknife.BindView;
import static com.benfranzi.mixit.cards.CardsPresenter.DRINK;

/**
 * View for the DrinkActivity. Responsible for presenting data, separated from the model by
 * the presenter.
 */
public class DrinkActivity extends BaseActivity<DrinkPresenter> implements DrinkContract.View {

    @BindView(R.id.drink_drinkview_iv)
    ImageView mDrinkViewIv;
    @BindView(R.id.drink_glass)
    TextView mGlassTv;
    @BindView(R.id.drink_category)
    TextView mCategoryTv;
    @BindView(R.id.drink_ingredients_tv)
    TableLayout mIngredientsTl;
    @BindView(R.id.drink_instructions)
    TextView mInstructionsTv;
    @BindView(R.id.drink_favourite_fab)
    FloatingActionButton mFavouriteFab;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.drink_linearlayout_ll)
    LinearLayout mItemsLl;

    /**
     * Returns layout to be inflated.
     */
    @Override
    protected int getContentResource() {
        return R.layout.activity_drink;
    }

    /**
     * Initial setup, called on activity create.
     */
    @Override
    protected void init(@Nullable Bundle state) {
        setSupportActionBar(mToolbar);

        mFavouriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().changeFavourite(true);
            }
        });
        Intent i = getIntent();
        Drink mDrink = (Drink) i.getSerializableExtra(DRINK);
        Glide
                .with(this)
                .load(mDrink.getImage())
                .apply(new RequestOptions()
                 .centerCrop())
                .into(mDrinkViewIv);
        getPresenter().initialLoad(getIntent());
    }

    /**
     * Injects dependencies.
     */
    @Override
    public void injectDependencies() {
        getActivityComponent().injectDrink(this);
    }

    /**
     * Toggles the listview on loading to prevent artifacts.
     */
    @Override
    public void toggleProgressBar(boolean visible) {
        if (visible) {
            mItemsLl.setVisibility(View.GONE);
        } else {
            mItemsLl.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Presents the Drink item values passed from the presenter in the view.
     */
    @Override
    public void updateUI(String name, String glass, String category, String instructions, ArrayList<String> measures, ArrayList<String> ingredients, boolean favourite) {

        mToolbarLayout.setTitle(name);
        mGlassTv.setText(glass);
        mCategoryTv.setText(category);
        mInstructionsTv.setText(instructions);

        if (!favourite) {
            mFavouriteFab.setImageResource(R.drawable.ic_add_black_24dp);
        } else {
            mFavouriteFab.setImageResource(R.drawable.ic_star_black_24dp);
        }
        mIngredientsTl.removeAllViews();
        TableRow row;
        TextView measure;
        TextView ingredient;
        for (int n = 0; n < ingredients.size(); n++) {
            row = new TableRow(this);
            if (n < measures.size()) {
                measure = new TextView(this);
                measure.setText(measures.get(n));
                measure.setGravity(Gravity.RIGHT);
                row.addView(measure);
            }
            ingredient = new TextView(this);
            ingredient.setText(ingredients.get(n));
            row.addView(ingredient);
            mIngredientsTl.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            );
        }
    }
}
