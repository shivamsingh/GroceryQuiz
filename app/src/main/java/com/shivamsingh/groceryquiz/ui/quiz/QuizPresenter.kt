package com.shivamsingh.groceryquiz.ui.quiz

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.shivamsingh.groceryquiz.domain.QuizRepository
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class QuizPresenter @Inject constructor(val quizRepository: QuizRepository) : MviBasePresenter<QuizView, QuizViewModel>() {
    override fun bindIntents() {
        val submitQuiz = intent(QuizView::submitQuizIntent)
                .flatMap(quizRepository::submit)
                .map { AnswerSubmitted(it) }

        val nextQuiz = intent(QuizView::nextQuizIntent)
                .flatMap { ignored -> quizRepository.next() }
                .map { NextQuiz(it) }

        val timedOut = intent(QuizView::timedOutQuizIntent)
                .map { TimedOut() }

        val retakeQuiz = intent(QuizView::retakeQuizIntent)
                .flatMap { ignored -> quizRepository.retake() }
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
        val activeAnseweredQuizIntent = PublishSubject.create<Boolean>()
        val activeTimedOudQuizIntent = PublishSubject.create<Boolean>()

        val activeQuiz = intent(QuizView::activeQuizIntent)
                .flatMap { ignored -> quizRepository.active().subscribeOn(Schedulers.io()) }
                .map { ActiveQuiz(it, quizRepository.activeQuizStartTime()) }
                .doOnComplete {
                    activeAnseweredQuizIntent.onNext(true)
                    activeTimedOudQuizIntent.onNext(true)
                }

        val activeAnsweredQuiz = intent { activeAnseweredQuizIntent }
                .flatMap { ignored -> quizRepository.activeAnsweredOption() }
                .map { AnswerSubmitted(it) }

        val activeTimedOutQuiz = intent { activeAnseweredQuizIntent }
                .flatMap { ignored -> quizRepository.timedOutActiveQuiz() }
                .map { TimedOut() }

        return Observable.merge(activeQuiz, activeAnsweredQuiz, activeTimedOutQuiz)
    }

    private fun initialState() = QuizViewModel(QuizViewState.LOADING, null, null)
}