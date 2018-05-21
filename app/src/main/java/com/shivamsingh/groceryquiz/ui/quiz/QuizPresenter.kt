package com.shivamsingh.groceryquiz.ui.quiz

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.shivamsingh.groceryquiz.domain.IQuizInteractor
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import javax.inject.Inject

class QuizPresenter @Inject constructor(val quizInteractor: IQuizInteractor) : MviBasePresenter<QuizView, QuizViewModel>() {
    override fun bindIntents() {
        val submitQuiz = intent(QuizView::submitQuizIntent)
                .flatMap(quizInteractor::submit)
                .map { AnswerSubmitted(it) }

        val nextQuiz = intent(QuizView::nextQuizIntent)
                .flatMap { ignored -> quizInteractor.next() }
                .map { NextQuiz(it) }

        val timedOut = intent(QuizView::timedOutQuizIntent)
                .map { TimedOut() }

        val retakeQuiz = intent(QuizView::retakeQuizIntent)
                .flatMap { ignored -> quizInteractor.retake() }
                .map { RetakeQuiz(it) }

        val allIntentsObservable =
                Observable.merge(submitQuiz, nextQuiz, retakeQuiz, timedOut)
                        .mergeWith(activeQuizIntents())
                        .observeOn(AndroidSchedulers.mainThread());

        subscribeViewState(allIntentsObservable
                .scan(initialState(), QuizViewModelReducer::reduce)
                .distinctUntilChanged(),
                QuizView::render)
    }

    private fun activeQuizIntents(): ObservableSource<out PartialViewModelChange>? {
        val activeAnseweredQuizIntent = SingleSubject.create<Boolean>()
        val activeTimedOudQuizIntent = SingleSubject.create<Boolean>()

        val activeQuiz = intent(QuizView::activeQuizIntent)
                .flatMap { ignored -> quizInteractor.active().subscribeOn(Schedulers.io()) }
                .map { ActiveQuiz(it, quizInteractor.activeQuizStartTime()) }
                .doOnComplete {
                    activeAnseweredQuizIntent.onSuccess(true)
                    activeTimedOudQuizIntent.onSuccess(true)
                }

        val activeAnsweredQuiz = intent { activeAnseweredQuizIntent.toObservable() }
                .flatMap { ignored -> quizInteractor.activeAnsweredOption() }
                .map { AnswerSubmitted(it) }

        val activeTimedOutQuiz = intent { activeTimedOudQuizIntent.toObservable() }
                .flatMap { ignored -> quizInteractor.timedOutActiveQuiz() }
                .map { TimedOut() }

        return Observable.merge(activeQuiz, activeAnsweredQuiz, activeTimedOutQuiz)
    }

    private fun initialState() = QuizViewModel(QuizViewState.LOADING, null, null)
}