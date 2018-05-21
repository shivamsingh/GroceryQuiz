package com.shivamsingh.groceryquiz.ui.quiz

import java.util.*

object QuizViewModelReducer {

    fun reduce(oldModel: QuizViewModel, partialChange: PartialViewModelChange): QuizViewModel {
        if (partialChange is Loading)
            return oldModel.copy(state = QuizViewState.LOADING)
        if (partialChange is ActiveQuiz)
            return oldModel.copy(state = QuizViewState.ACTIVE, quiz = partialChange.quiz, startTime = partialChange.startTime)
        if (partialChange is NextQuiz)
            return oldModel.copy(state = QuizViewState.ACTIVE, quiz = partialChange.quiz, startTime = partialChange.startTime)
        if (partialChange is AnswerSubmitted)
            return oldModel.copy(state = QuizViewState.ANSWERED, answeredOption = partialChange.answeredOption)
        if (partialChange is RetakeQuiz)
            return oldModel.copy(state = QuizViewState.ACTIVE, quiz = partialChange.quiz, startTime = partialChange.startTime)
        if (partialChange is TimedOut)
            return oldModel.copy(state = QuizViewState.TIMED_OUT)
        return oldModel;
    }
}