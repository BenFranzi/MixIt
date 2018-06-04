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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.benfranzi.mixit.R;
import com.benfranzi.mixit.cards.CardsActivity;
import com.benfranzi.mixit.drink.DrinkActivity;
import com.benfranzi.mixit.model.Drink;
import static com.benfranzi.mixit.cards.CardsPresenter.DRINK;

/**
 * Provides notification functionality for the application.
 */
public class DrinksNotificationsManager {
    public static final String TITLE_CONST = "It's time for a ";
    public static final String CHANNEL_ID = "suggestion_channel";
    public static final int NOTIFICATION_ID = 101;
    private Context mContext;
    private static DrinksNotificationsManager mInstance;

    /**
     * Follows the singleton pattern, gets an instance of the Drinks Notification Manager.
     */
    public synchronized static DrinksNotificationsManager get(Context context) {
        if (mInstance == null) {
            mInstance = new DrinksNotificationsManager(context);
        }
        return mInstance;
    }

    /**
     * Constructor that sets the context and creates the notification channel.
     */
    public DrinksNotificationsManager(Context context) {
        mContext = context;
        createNotificationChannel();
    }

    /**
     * Makes a notification. Either for a drink or a generic message if drink is null.
     */
    public void make(Drink drink) {
        // Create an Intent for the activity you want to start
        Intent drinkResultIntent = new Intent(mContext, DrinkActivity.class);
        drinkResultIntent.putExtra(DRINK, drink);
        Intent comebackResultIntent = new Intent(mContext, CardsActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        if (drink != null) {
            stackBuilder.addNextIntentWithParentStack(drinkResultIntent);
        } else {
            stackBuilder.addNextIntentWithParentStack(comebackResultIntent);
        }
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder;
        if (drink != null) {
            builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_local_bar_black_24dp)
                    .setContentTitle(TITLE_CONST + drink.getName())
                    .setContentText(mContext.getString(R.string.notification_message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else {
            builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_local_bar_black_24dp)
                    .setContentTitle(mContext.getString(R.string.comeback_messsage))
                    .setContentText(mContext.getString(R.string.comeback_dialog))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        builder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }

    /**
     * Creates the notification channel for the make function.
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
