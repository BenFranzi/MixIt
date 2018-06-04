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

package com.benfranzi.mixit.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.benfranzi.mixit.di.component.ActivityComponent;
import com.benfranzi.mixit.di.component.DaggerActivityComponent;
import com.benfranzi.mixit.di.module.ActivityModule;
import com.benfranzi.mixit.services.SuggestionService;
import javax.inject.Inject;
import butterknife.ButterKnife;

/**
 * Provides View MVP functionality to activities in the application.
 * Based on this example:
 * @see <a href="https://github.com/faruktoptas/news-mvp">Faruk Toptas - news MVP</a>
 */

public abstract class BaseActivity<T extends BaseMvpPresenter> extends AppCompatActivity {

    private static int activities = 0;
    /**
     * Injected presenter.
     */
    @Inject
    T mPresenter;

    private ActivityComponent mActivityComponent;

    /**
     * Generate bound components and inject dependencies.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResource());
        ButterKnife.bind(this);
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();
        injectDependencies();
        mPresenter.attach(this);
        init(savedInstanceState);
    }

    /**
     * Detach presenter.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    /**
     * Returns the activity component.
     */
    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    /**
     * Getter for the presenter.
     */
    public T getPresenter() {
        return mPresenter;
    }

    /**
     * Layout resource to be inflated.
     */
    @LayoutRes
    protected abstract int getContentResource();

    /**
     * Initializations.
     */
    protected abstract void init(@Nullable Bundle state);

    /**
     * Injecting dependencies.
     */
    protected abstract void injectDependencies();


    /**
     * increments activities loaded to keep track of app exit.
     */
    @Override
    protected void onStart() {
        super.onStart();
        activities = activities + 1;
    }

    /**
     * decrements activities loaded to keep track of app exit. If the app has been
     * closed then it will notify the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        activities = activities - 1;
        if (activities == 0) {
            new SuggestionService(this).startAlarm();
        }
    }
}