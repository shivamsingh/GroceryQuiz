package com.shivamsingh.groceryquiz

import android.app.Activity
import android.app.Application
import android.os.StrictMode
import com.facebook.stetho.Stetho
import com.shivamsingh.groceryquiz.di.DaggerAppComponent
import com.shivamsingh.groceryquiz.platform.logging.ErrorReportingTree
import com.squareup.leakcanary.LeakCanary
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class GroceryQuizApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = activityDispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        initDependecyInjection()
        configure()
    }

    private fun initDependecyInjection() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    private fun configure() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            StrictMode.enableDefaults()
            Stetho.initializeWithDefaults(this)
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return
            }
            LeakCanary.install(this)
        } else
            Timber.plant(ErrorReportingTree())
    }
}