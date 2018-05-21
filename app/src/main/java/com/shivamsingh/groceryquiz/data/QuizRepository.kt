package com.shivamsingh.groceryquiz.data

import android.content.Context
import com.shivamsingh.groceryquiz.domain.entity.QuizModel
import com.shivamsingh.groceryquiz.ui.entity.Quiz
import io.reactivex.Observable

interface QuizRepository {

    fun allQuiz(): Observable<List<QuizModel>>
}