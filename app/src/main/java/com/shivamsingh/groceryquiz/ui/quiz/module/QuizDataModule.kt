package com.shivamsingh.groceryquiz.ui.quiz.module

import com.aasaanjobs.partnerinternal.di.scopes.PerActivity
import com.shivamsingh.groceryquiz.data.InMemoryQuizRepository
import com.shivamsingh.groceryquiz.data.disc.SharedPreferencesStore
import com.shivamsingh.groceryquiz.domain.QuizInteractor
import com.shivamsingh.groceryquiz.domain.IQuizInteractor
import dagger.Module
import dagger.Provides

@Module
class QuizDataModule {

    @Provides
    @PerActivity
    fun quizRepository(store: SharedPreferencesStore, quizDatabase: InMemoryQuizRepository): IQuizInteractor {
        return QuizInteractor(store, quizDatabase)
    }
}