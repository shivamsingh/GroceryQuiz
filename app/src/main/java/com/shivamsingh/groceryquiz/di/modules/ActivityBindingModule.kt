package com.shivamsingh.groceryquiz.di.modules

import com.aasaanjobs.partnerinternal.di.scopes.PerActivity
import com.shivamsingh.groceryquiz.ui.MainActivity
import com.shivamsingh.groceryquiz.ui.quiz.module.QuizDataModule
import com.shivamsingh.groceryquiz.ui.quiz.module.QuizModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [(QuizModule::class), (QuizDataModule::class)])
    abstract fun mainActivity(): MainActivity
}