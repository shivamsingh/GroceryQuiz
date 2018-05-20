package com.shivamsingh.groceryquiz.domain

import com.shivamsingh.groceryquiz.ui.entity.AnsweredOption
import com.shivamsingh.groceryquiz.ui.entity.Quiz
import io.reactivex.Observable

interface QuizRepository {

    fun active(): Observable<Quiz>

    fun submit(answer: String): Observable<AnsweredOption>

    fun next(): Observable<Quiz>

    fun retake(): Observable<Quiz>

    fun activeQuizStartTime(): Long

    fun activeAnsweredOption(): Observable<AnsweredOption>

    fun timedOutActiveQuiz(): Observable<Boolean>
}