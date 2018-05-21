package com.shivamsingh.groceryquiz.ui.quiz

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface QuizView : MvpView{

    fun activeQuizIntent(): Observable<Boolean>

    fun submitQuizIntent(): Observable<String>

    fun nextQuizIntent(): Observable<Boolean>

    fun retakeQuizIntent(): Observable<Boolean>

    fun timedOutQuizIntent(): Observable<Boolean>

    fun render(viewState: QuizViewModel)
}