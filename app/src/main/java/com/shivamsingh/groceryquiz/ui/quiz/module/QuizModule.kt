package com.shivamsingh.groceryquiz.ui.quiz.module

import com.aasaanjobs.partnerinternal.di.scopes.PerFragment
import com.shivamsingh.groceryquiz.ui.quiz.QuizFragment
import com.shivamsingh.groceryquiz.ui.quiz.QuizView
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class QuizModule {

    @PerFragment
    @ContributesAndroidInjector
    abstract fun quizFragment(): QuizFragment

    @Binds
    @PerFragment
    abstract fun quizView(postsFragment: QuizFragment): QuizView
}