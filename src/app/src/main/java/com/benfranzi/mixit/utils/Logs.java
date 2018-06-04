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

import android.util.Log;

/**
 * Logger class to provide generic log expressions
 */
public class Logs {
    private static final String TAG = "MIXIT";

    /**
     * Generic logv for the application.
     */
    public static void logv(String log) {
        Log.v(TAG, log);
    }

    /**
     * Generic logwtf for the application.
     */
    public static void logwtf(String log) {
        Log.wtf(TAG, log);
    }
}