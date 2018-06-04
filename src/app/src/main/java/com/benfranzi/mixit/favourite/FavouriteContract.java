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
import com.benfranzi.mixit.base.BaseMvpPresenter;
import com.benfranzi.mixit.base.BaseView;

/**
 * Contract for functions made publicly accessible in the Favourite MVP.
 */
public class FavouriteContract {
    // Implemented in the presenter, called by the view
    interface Presenter extends BaseMvpPresenter<FavouriteContract.View> {
        void updateUI();
        void onLoadActivity();
    }

    // Implemented in the view, called by the presenter
    interface View extends BaseView {
        Context getContext();
        void setupRecyclerView(FavouriteAdapter favouriteAdapter);
        void toggleProgressBar(boolean visible);
    }
}
