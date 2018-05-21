package com.shivamsingh.groceryquiz.ui.quiz.module

import com.aasaanjobs.partnerinternal.di.scopes.PerActivity
import com.shivamsingh.groceryquiz.data.InMemoryQuizDatabase
import com.shivamsingh.groceryquiz.data.disc.SharedPreferencesStore
import com.shivamsingh.groceryquiz.domain.InMemoryQuizRepository
import com.shivamsingh.groceryquiz.domain.QuizRepository
import dagger.Module
import dagger.Provides

@Module
class QuizDataModule {

    @Provides
    @PerActivity
    fun quizRepository(store: SharedPreferencesStore, quizDatabase: InMemoryQuizDatabase): QuizRepository {
        return InMemoryQuizRepository(store, quizDatabase)
    }
}