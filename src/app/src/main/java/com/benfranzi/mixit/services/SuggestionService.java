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

package com.benfranzi.mixit.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

/**
 * Suggestion service. Starts the alarm broadcast listener to trigger notification
 */
public class SuggestionService {
    private Context context;
    private PendingIntent mAlarmSender;
    public static final int TIMEOUT = 10; //30 seconds


    /**
     * Constructor providing initial configuration
     */
    public SuggestionService(Context context) {
        this.context = context;
        mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
    }

    /**
     * Starts the alarm timeout. After TIMEOUT the alarm receiver is triggered.
     */
    public void startAlarm(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, TIMEOUT);
        long firstTime = c.getTimeInMillis();
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
    }
}

