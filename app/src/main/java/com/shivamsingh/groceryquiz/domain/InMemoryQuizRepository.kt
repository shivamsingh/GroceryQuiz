package com.shivamsingh.groceryquiz.domain

import com.shivamsingh.groceryquiz.data.QuizDatabase
import com.shivamsingh.groceryquiz.data.disc.DictionaryStore
import com.shivamsingh.groceryquiz.domain.entity.QuizModel
import com.shivamsingh.groceryquiz.ui.entity.AnsweredOption
import com.shivamsingh.groceryquiz.ui.entity.Quiz
import io.reactivex.Observable
import java.util.*

class InMemoryQuizRepository(val store: DictionaryStore, val quizDatabase: QuizDatabase) : QuizRepository {
    private var activeQuiz: QuizModel? = null

    override fun active(): Observable<Quiz> {
        return activeQuiz()
                .map { toShuffledOptions(it) }
                .map { toQuiz(it) }
                .switchIfEmpty(next())
    }

    override fun activeQuizStartTime(): Long {
        return activeQuiz()
                .map { store.retrieveLong(ACTIVE_QUIZ_KEY_START_TIME) }
                .blockingFirst()
    }

    override fun activeAnsweredOption(): Observable<AnsweredOption> {
        return activeQuiz()
                .filter { it.question == store.retrieveValue(LAST_ANSWERED_QUIZ_KEY) }
                .map { toAnsweredOption(store.retrieveValue(LAST_ANSWERED_QUIZ_OPTION)) }
    }

    override fun timedOutActiveQuiz(): Observable<Boolean> {
        return activeQuiz()
                .filter { it.question != store.retrieveValue(LAST_ANSWERED_QUIZ_KEY) }
                .filter { isActiveQuizTimedOut() }
                .map { true }
    }

    private fun activeQuiz(): Observable<QuizModel> {
        if (activeQuiz != null) Observable.just(activeQuiz)
        return quizDatabase.allQuiz()
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
        return quizDatabase.allQuiz()
                .flatMap { Observable.just(it.toMutableList().shuffled().first()) }
                .doOnNext { storeAsActive(it) }
                .map { toShuffledOptions(it) }
                .map { toQuiz(it) }
    }

    override fun retake(): Observable<Quiz> {
        return Observable.just(activeQuiz)
                .map { toShuffledOptions(it) }
                .map { toQuiz(it) }
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

    private fun isActiveQuizTimedOut() =
            (Calendar.getInstance().timeInMillis - store.retrieveLong(ACTIVE_QUIZ_KEY_START_TIME)) / 1000 >= QUIZ_TIMEOUT_IN_SECONDS

    private fun toShuffledOptions(quizModel: QuizModel) =
            quizModel.copy(options = quizModel.options.shuffled())

    private fun toQuiz(it: QuizModel) =
            Quiz(it.question, it.options[0].option, it.options[1].option, it.options[2].option, it.options[3].option)

    private fun toAnsweredOption(answer: String) = AnsweredOption(answer, activeQuiz!!.correctOption.option == answer)

    companion object {
        const val QUIZ_TIMEOUT_IN_SECONDS = 30
    }
}