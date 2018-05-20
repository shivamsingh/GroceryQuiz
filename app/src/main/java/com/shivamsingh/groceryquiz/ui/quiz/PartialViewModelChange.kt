package com.shivamsingh.groceryquiz.ui.quiz

import com.shivamsingh.groceryquiz.ui.entity.AnsweredOption
import com.shivamsingh.groceryquiz.ui.entity.Quiz
import java.util.*

sealed class PartialViewModelChange

class Loading : PartialViewModelChange()

class ActiveQuiz(val quiz: Quiz, val startTime: Long = currentTimeInMillis()) : PartialViewModelChange()

data class AnswerSubmitted(val answeredOption: AnsweredOption) : PartialViewModelChange()

class TimedOut : PartialViewModelChange()

class NextQuiz(val quiz: Quiz, val startTime: Long = currentTimeInMillis()) : PartialViewModelChange()

data class RetakeQuiz(val quiz: Quiz) : PartialViewModelChange()

private fun currentTimeInMillis() = Calendar.getInstance().timeInMillis