package com.shivamsingh.groceryquiz.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.shivamsingh.groceryquiz.domain.entity.QuizModel
import com.shivamsingh.groceryquiz.domain.entity.QuizOptionModel
import io.reactivex.Observable
import java.util.*

class InMemoryQuizDatabase(val context: Context) : QuizDatabase {

    private var quizes: List<QuizModel>? = null

    override fun allQuiz(): Observable<List<QuizModel>> {
        if (quizes == null)
            return Observable.fromCallable { loadAllQuiz() }
        return Observable.just(quizes)
    }

    private fun loadAllQuiz(): List<QuizModel> {
        // Sleep thread to simulate heavy IO operation and to show Loading state.
        Thread.sleep(3 * 1000)
        val quizesJson = context.assets.open("zquestions.json").bufferedReader().use { it.readText() }
        quizes = quizHashMap(quizesJson).map { toQuizModel(it) }.toList()
        return quizes as List<QuizModel>
    }

    private fun quizHashMap(quizesJson: String) =
            (GsonBuilder().create().fromJson(quizesJson, LinkedHashMap::class.java) as LinkedHashMap<String, ArrayList<String>>)

    private fun toQuizModel(it: Map.Entry<String, ArrayList<String>>): QuizModel {
        return QuizModel(it.key, options(it), correctOption(it), QUIZ_TIMEOUT_IN_SECONDS)
    }

    private fun correctOption(it: Map.Entry<String, ArrayList<String>>) =
            QuizOptionModel(it.value[0])

    private fun options(it: Map.Entry<String, ArrayList<String>>): List<QuizOptionModel> {
        return listOf(QuizOptionModel(it.value[0]),
                QuizOptionModel(it.value[1]),
                QuizOptionModel(it.value[2]),
                QuizOptionModel(it.value[3]))
    }

    companion object {
        const val QUIZ_TIMEOUT_IN_SECONDS = 30
    }
}
