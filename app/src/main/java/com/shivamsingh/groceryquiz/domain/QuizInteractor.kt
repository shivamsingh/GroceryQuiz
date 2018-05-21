package com.shivamsingh.groceryquiz.domain

import com.shivamsingh.groceryquiz.data.QuizRepository
import com.shivamsingh.groceryquiz.data.disc.DictionaryStore
import com.shivamsingh.groceryquiz.domain.entity.QuizModel
import com.shivamsingh.groceryquiz.ui.entity.AnsweredOption
import com.shivamsingh.groceryquiz.ui.entity.Quiz
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class QuizInteractor @Inject constructor(val store: DictionaryStore, val quizRepository: QuizRepository) : IQuizInteractor {
    private var activeQuiz: QuizModel? = null

    override fun active(): Observable<Quiz> {
        return activeQuiz()
                .map { toShuffledOptions(it) }
                .map { toQuiz(it) }
                .switchIfEmpty(next())
    }

    private fun activeQuiz(): Observable<QuizModel> {
        if (activeQuiz != null) return Observable.just(activeQuiz)
        return quizRepository.allQuiz()
                .flatMap { Observable.fromIterable(it) }
                .filter { it.question == storedActiveQuizQuestion() }
                .doOnNext { activeQuiz = it }
    }

    override fun submit(answer: String): Observable<AnsweredOption> {
        return Observable
                .just(toAnsweredOption(answer))
                .doOnNext { storeAsLastAnsweredQuiz(it) }
    }

    override fun next(): Observable<Quiz> {
        return quizRepository.allQuiz()
                .flatMap { Observable.just(it.toMutableList().shuffled().first()) }
                .doOnNext { storeAsActive(it) }
                .map { toShuffledOptions(it) }
                .map { toQuiz(it) }
    }

    override fun retake(): Observable<Quiz> {
        return Observable.just(activeQuiz)
                .doOnNext { clearLastAnswered() }
                .doOnNext { storeAsActive(it!!) }
                .map { toShuffledOptions(it) }
                .map { toQuiz(it) }
    }

    override fun activeQuizStartTime() = store.retrieveLong(ACTIVE_QUIZ_KEY_START_TIME)

    override fun activeAnsweredOption(): Observable<AnsweredOption> {
        return activeQuiz()
                .filter { it.question == store.retrieveValue(LAST_ANSWERED_QUIZ_KEY) }
                .map { toAnsweredOption(store.retrieveValue(LAST_ANSWERED_QUIZ_OPTION)) }
    }

    override fun timedOutActiveQuiz(): Observable<Boolean> {
        return activeQuiz()
                .filter { it.question != store.retrieveValue(LAST_ANSWERED_QUIZ_KEY) }
                .filter { isActiveQuizTimedOut(it) }
                .map { true }
    }

    private fun storedActiveQuizQuestion() = store.retrieveValue(ACTIVE_QUIZ_KEY)

    private fun storeAsActive(quizModel: QuizModel) {
        activeQuiz = quizModel
        store.storeValue(ACTIVE_QUIZ_KEY, quizModel.question)
        store.storeValue(ACTIVE_QUIZ_KEY_START_TIME, Calendar.getInstance().timeInMillis)
    }

    private fun storeAsLastAnsweredQuiz(answeredOption: AnsweredOption) {
        store.storeValue(LAST_ANSWERED_QUIZ_KEY, activeQuiz!!.question)
        store.storeValue(LAST_ANSWERED_QUIZ_OPTION, answeredOption.option)
        store.storeValue(IS_LAST_ANSWERED_QUIZ_OPTION_CORRECT, answeredOption.isCorrect)
    }

    private fun clearLastAnswered() {
        store.storeValue(LAST_ANSWERED_QUIZ_KEY, "")
        store.storeValue(LAST_ANSWERED_QUIZ_OPTION, "")
        store.storeValue(IS_LAST_ANSWERED_QUIZ_OPTION_CORRECT, "")
    }

    private fun isActiveQuizTimedOut(it: QuizModel) =
            (Calendar.getInstance().timeInMillis - store.retrieveLong(ACTIVE_QUIZ_KEY_START_TIME)) / 1000 >= it.timeoutInSeconds

    private fun toShuffledOptions(quizModel: QuizModel) =
            quizModel.copy(options = quizModel.options.shuffled())

    private fun toQuiz(it: QuizModel) =
            Quiz(it.question, it.options[0].option, it.options[1].option, it.options[2].option, it.options[3].option, it.timeoutInSeconds)

    private fun toAnsweredOption(answer: String) = AnsweredOption(answer, activeQuiz!!.correctOption.option == answer)
}