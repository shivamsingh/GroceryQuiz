package com.shivamsingh.groceryquiz.di

import android.app.Application
import com.aasaanjobs.partnerinternal.di.scopes.PerApplication
import com.shivamsingh.groceryquiz.GroceryQuizApplication
import com.shivamsingh.groceryquiz.di.modules.ActivityBindingModule
import com.shivamsingh.groceryquiz.di.modules.ApplicationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication

@PerApplication
@Component(modules = [
    ActivityBindingModule::class,
    ApplicationModule::class,
    AndroidSupportInjectionModule::class
])
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: GroceryQuizApplication)

    override fun inject(instance: DaggerApplication)
}
