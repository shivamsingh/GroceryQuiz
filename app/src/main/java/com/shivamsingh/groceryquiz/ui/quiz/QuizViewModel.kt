package com.shivamsingh.groceryquiz.ui.quiz

import com.shivamsingh.groceryquiz.ui.entity.AnsweredOption
import com.shivamsingh.groceryquiz.ui.entity.Quiz

data class QuizViewModel(val state: QuizViewState,
                         val quiz: Quiz?,
                         val answeredOption: AnsweredOption?,
                         val startTime: Long = 0)

enum class QuizViewState {
    LOADING,
    ACTIVE,
    ANSWERED,
    TIMED_OUT
}
