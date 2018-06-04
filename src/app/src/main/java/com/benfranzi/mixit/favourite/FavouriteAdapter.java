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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.drink.DrinkActivity;
import com.benfranzi.mixit.model.Drink;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import static com.benfranzi.mixit.cards.CardsPresenter.DRINK;

/**
 * Adapter for the favourites recyclerview. Provides the favourite items for presentation on the
 * favourites activity.
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private ArrayList<Drink> mDrinks;
    Context mContext;

    /**
     * Constructor for the favourite adapter, sets the context and individual drink.
     */
    public FavouriteAdapter(Context context, ArrayList<Drink> drinks) {
        mContext = context;
        mDrinks = drinks;
    }

    /**
     * Returns the item count.
     */
    @Override
    public int getItemCount() {
        return mDrinks.size();
    }

    /**
     * Inflates the view holder.
     */
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
        FavouriteAdapter.ViewHolder viewHolder = new FavouriteAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    /**
     * Binds favourite to viewholder item in the favourites recyclerview.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Drink drink = mDrinks.get(position);
        Glide
                .with(mContext)
                .load(drink.getImage())
                .apply(new RequestOptions()
                        .centerCrop())
                .into(holder.mImageIv);
        holder.mNameTv.setText(drink.getName());
        holder.mCategoryTv.setText(drink.getCategory());
    }

    /**
     * Class for the view holder. Exposes the holder objects.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageIv;
        private TextView mNameTv;
        private TextView mCategoryTv;
        private LinearLayout mFavouriteLl;

        /**
         * Constructor for the view holder that sets the objects.
         */
        public ViewHolder(View v) {
            super(v);
            mImageIv = (ImageView) v.findViewById(R.id.favourite_item_image_iv);
            mNameTv = (TextView) v.findViewById(R.id.favourite_item_name_tv);
            mCategoryTv = (TextView) v.findViewById(R.id.favourite_item_category_tv);
            mFavouriteLl = (LinearLayout) v.findViewById(R.id.favourite_item_ll);
            mFavouriteLl.setOnClickListener(this);
        }

        /**
         * Opens the selected drink on recyclerview click item.
         */
        @Override
        public void onClick(View v) {
            Intent i = new Intent(mContext, DrinkActivity.class);
            i.putExtra(DRINK, mDrinks.get(getAdapterPosition()));
            mContext.startActivity(i);
        }
    }
}
