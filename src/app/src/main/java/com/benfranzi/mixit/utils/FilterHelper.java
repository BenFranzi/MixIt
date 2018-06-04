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

import com.benfranzi.mixit.model.Drink;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class to generate a filter list and to filter out the drinks list based on the input filter state.
 */
public class FilterHelper {
    public static final String FILTER_VODKA[] = {"lemon vodka", "vodka", "peach vodka", "absolut vodka", "vanilla vodka", "cranberry vodka", "raspberry vodka"};
    public static final String FILTER_GIN[] = {"gin", "sloe gin"};
    public static final String FILTER_WHISKEY[] =  {"scotch", "southern comfort", "brandy", "bourbon", "whiskey"};
    public static final String FILTER_RUM[] = {"rum", "light rum", "dark rum", "añejo rum", "spiced rum", "151 proof rum", "malibu rum", "coconut rum", "white rum"};
    public static final String FILTER_TEQUILA[] = {"tequila"};
    public static final String[] FILTER_CONSTANTS[] = {FILTER_VODKA, FILTER_GIN, FILTER_WHISKEY, FILTER_RUM, FILTER_TEQUILA};

    /**
     * Returns a list of filtered drinks based on the filter state.
     */
    public static ArrayList<Drink> filter(ArrayList<Drink> drinks, boolean filter[]) {
        ArrayList<Drink> outList = new ArrayList<Drink>();
        ArrayList<String> filterList = new ArrayList<String>();
        List<String> temp;

        if (filter[0]) {
            temp = Arrays.asList(FILTER_VODKA);
            filterList.addAll(temp);
        }
        if (filter[1]) {
            temp = Arrays.asList(FILTER_GIN);
            filterList.addAll(temp);
        }
        if (filter[2]) {
            temp = Arrays.asList(FILTER_WHISKEY);
            filterList.addAll(temp);
        }
        if (filter[3]) {
            temp = Arrays.asList(FILTER_RUM);
            filterList.addAll(temp);
        }
        if (filter[4]) {
            temp = Arrays.asList(FILTER_TEQUILA);
            filterList.addAll(temp);
        }
        if (filterList.size() == 0) {
            return drinks;
        }
        for (Drink drink : drinks) {
            ArrayList<String> ingredients = new ArrayList<String>();
            ingredients.addAll(drink.getIngredients());
            if (checkMatch(Helpers.toLower(ingredients), filter)) {
                outList.add(drink);
            }
        }
        return outList;
    }

    /**
     * Finds matches where all of the filter states are met
     */
    private static boolean checkMatch(ArrayList<String> ingredients, boolean filter[]) {
        //find all matches of filter in ingredients
        ArrayList<String> tempIngredients = new ArrayList<String>();
        List<String> constants;
        boolean hasMatch = true;
        for (int n = 0; n < filter.length; n++) {
            if (filter[n]) {
                tempIngredients.removeAll(tempIngredients);
                tempIngredients.addAll(ingredients);
                constants = Arrays.asList(FILTER_CONSTANTS[n]);
                tempIngredients.retainAll(constants);
                if (tempIngredients.size() == 0) {
                    hasMatch = false;
                    break;
                }
            }
        }
        return hasMatch;
    }
}
