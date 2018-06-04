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

package com.benfranzi.mixit.model;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Drink model class. Provides basic get functions for the model.
 */
public class Drink implements Serializable {
    private String mName;
    private String mGlass;
    private String mCategory;
    private String mInstructions;
    private URL mImage;
    private ArrayList<String> mIngredients;
    private ArrayList<String> mMeasures;

    public Drink(String name,
                 String glass,
                 String category,
                 String instructions,
                 URL image,
                 ArrayList<String> ingredients,
                 ArrayList<String> measures) {
        this.mName = name;
        this.mGlass = glass;
        this.mCategory = category;
        this.mInstructions = instructions;
        this.mImage = image;
        this.mIngredients = ingredients;
        this.mMeasures = measures;
    }

    public String getName() {
        return mName;
    }

    public String getGlass() {
        return mGlass;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getInstructions() {
        return mInstructions;
    }

    public URL getImage() {
        return mImage;
    }

    public ArrayList<String> getIngredients() {
        return mIngredients;
    }

    public ArrayList<String> getMeasures() {
        return mMeasures;
    }
}
