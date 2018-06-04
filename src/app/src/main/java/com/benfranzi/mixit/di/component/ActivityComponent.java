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

package com.benfranzi.mixit.di.component;

import com.benfranzi.mixit.cards.CardsActivity;
import com.benfranzi.mixit.di.module.ActivityModule;
import com.benfranzi.mixit.drink.DrinkActivity;
import com.benfranzi.mixit.favourite.FavouriteActivity;
import com.benfranzi.mixit.login.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Interface to implement injection.
 */
@Singleton
@Component(modules = {ActivityModule.class})
public interface ActivityComponent {

    void injectCards(CardsActivity obj);
    void injectDrink(DrinkActivity obj);
    void injectLogin(LoginActivity obj);
    void injectFavourite(FavouriteActivity obj);
}