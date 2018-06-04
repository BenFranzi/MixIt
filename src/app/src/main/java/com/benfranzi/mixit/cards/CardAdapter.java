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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.model.Drink;
import com.bumptech.glide.Glide;

/**
 * Adapter for the Card stack. Provides information for presentation on the
 * stack view.
 */
public class CardAdapter extends ArrayAdapter<Drink> {

    private Context mContext;

    /**
     * Constructor for the card adapter, sets the context.
     */
    public CardAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    /**
     * Sets up the view for the card.
     */
    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;
        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.drink_card, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        Drink drink = getItem(position);
        holder.name.setText(drink.getName());
        Glide.with(getContext()).load(drink.getImage()).into(holder.image);
        return contentView;
    }

    /**
     * Class for the view holder. Exposes the holder objects.
     */
    private static class ViewHolder {
        public TextView name;
        public ImageView image;

        /**
         * Constructor for the view holder that sets the objects.
         */
        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.item_drink_name);
            this.image = (ImageView) view.findViewById(R.id.item_drink_image);
        }
    }


}
