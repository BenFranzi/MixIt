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

import android.support.design.widget.Snackbar;
import android.view.View;

import com.benfranzi.mixit.model.Drink;
import com.google.firebase.database.DataSnapshot;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Helper functions that provide conversions between types.
 */
public class Helpers {

    /**
     * Converts drinks data snapshot to array list of drinks.
     */
    public static ArrayList<Drink> DrinksToPOJO(DataSnapshot data) {
        Map<String, Object> drinks = (Map<String,Object>) data.getValue();
        ArrayList<Drink> filteredDrinks = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : drinks.entrySet()) {

            //Get user map
            Map singleDrink = (Map) entry.getValue();
            Drink newDrink;

            URL imageUrl;
            try {
                imageUrl = new URL((String) singleDrink.get("image"));
            } catch (MalformedURLException e) {
                Logs.logv(e.getMessage());
                imageUrl = null;
            }
            newDrink = new Drink(
                    (String) singleDrink.get("name"),
                    (String) singleDrink.get("glass"),
                    (String) singleDrink.get("category"),
                    (String) singleDrink.get("instructions"),
                    imageUrl,
                    (ArrayList<String>) singleDrink.get("ingredients"),
                    (ArrayList<String>) singleDrink.get("measures"));

            //Get phone field and append to list
            filteredDrinks.add(newDrink);
        }
        Logs.logv(filteredDrinks.toString());
        return filteredDrinks;
    }

    /**
     * Maps the data to a String key-pair data structure.
     */
    public static Map<String, Boolean> favouritesToMap(DataSnapshot data) {
        Map<String, Boolean> favourites = (Map<String,Boolean>) data.getValue();
        return favourites;
    }

    /**
     * Converts favourites data snapshot to string array list.
     */
    public static ArrayList<String> getFavourites(DataSnapshot data) {
        Map<String, Boolean> favourites = favouritesToMap(data);
        ArrayList<String> out = new ArrayList<String>();
        if (favourites != null) {
            for (String key : favourites.keySet()) {
                if (favourites.get(key) == true) {
                    out.add(key);
                }
            }
        }
        return out;
    }

    /**
     * Takes in the favourites list and produces the drinks that match the list.
     */
    public static ArrayList<Drink> getDrinksFromFavourites(ArrayList<Drink> drinks, ArrayList<String> favourites) {
        ArrayList<Drink> drinksOut = new ArrayList<Drink>();
        for (Drink drink : drinks) {
            for (String favourite : favourites) {
                String drinkHash = Helpers.hash(drink.getName());
                if (drinkHash.equals(favourite)) {
                    drinksOut.add(drink);
                }
            }
        }
        return drinksOut;
    }

    /**
     * Makes a snackbar
     */
    public static void makeSnackbar(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Picks n random drinks from drinks list while filering by the filter state.
     */
    public static ArrayList<Drink> pickRandom(int count, ArrayList<Drink> drinks, boolean filter[]) {
        Random random = new Random();
        ArrayList<Drink> selectedDrinks = new ArrayList<Drink>();
        drinks = FilterHelper.filter(drinks, filter);
        if (drinks.size() == 0) {
            return null;
        }

        if (count > drinks.size()) {
            count = drinks.size();
        }

        for (int i = 0; i < count; i++) {
            int index = random.nextInt(drinks.size());
            Drink drink = drinks.get(index);
            selectedDrinks.add(drink);
            drinks.remove(drink);
        }
        return selectedDrinks;
    }

    /**
     * Convers string array list to lower case.
     */
    public static ArrayList<String> toLower(ArrayList<String> strings)
    {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext())
        {
            iterator.set(iterator.next().toLowerCase());
        }
        return strings;
    }

    /**
     * Generates hashes to accurately store the drinks in the database while ignoring symbols.
     * based on: https://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
     */
    public static String hash(String input) {
        String out = "";
        byte[] bytesOfMessage;
        try {
            bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytesOfMessage);
            BigInteger bigInt = new BigInteger(1,digest);
            out = bigInt.toString(16);
        } catch (UnsupportedEncodingException e) {
            Logs.logv("Failed to generate hash as characters can't be converted to UTF-8");
        } catch (NoSuchAlgorithmException e) {
            Logs.logwtf("MD5 doesn't exist, this should never occur");
        }
        return out;
    }
}
