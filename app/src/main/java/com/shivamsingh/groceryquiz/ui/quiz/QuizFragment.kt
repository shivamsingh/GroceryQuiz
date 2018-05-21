package com.shivamsingh.groceryquiz.ui.quiz

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.jakewharton.rxbinding2.view.RxView
import com.shivamsingh.groceryquiz.R
import com.shivamsingh.groceryquiz.ui.base.BaseFragment
import com.shivamsingh.groceryquiz.ui.entity.AnsweredOption
import com.shivamsingh.groceryquiz.ui.entity.Quiz
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QuizFragment @Inject constructor() : BaseFragment<QuizView, QuizPresenter>(), QuizView {
    @BindView(R.id.question)
    lateinit var question: TextView
    @BindView(R.id.option1)
    lateinit var option1: ImageView
    @BindView(R.id.option2)
    lateinit var option2: ImageView
    @BindView(R.id.option3)
    lateinit var option3: ImageView
    @BindView(R.id.option4)
    lateinit var option4: ImageView
    @BindView(R.id.submit)
    lateinit var submit: View
    @BindView(R.id.next)
    lateinit var next: View
    @BindView(R.id.retake)
    lateinit var retake: View
    @BindView(R.id.message)
    lateinit var message: TextView

    private var selectedOptionValue = ""
    private var quiz: Quiz? = null
    private var timeoutSubscription: Disposable? = null
    private val timedOutSubject = PublishSubject.create<Boolean>()

    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var quizPresenter: QuizPresenter

    override fun layoutRes() = R.layout.quiz_fragment

    @OnClick(R.id.option1, R.id.option2, R.id.option3, R.id.option4)
    fun onClick(view: View) {
        selectOption(view)
        selectedOptionValue = selectedOptionValue(view)
    }

    private fun selectedOptionValue(view: View): String {
        if (quiz == null) throw IllegalStateException("Quiz shouldn't be null in QuizViewState.ACTIVE state")
        when (view) {
            option1 -> return quiz!!.option1
            option2 -> return quiz!!.option2
            option3 -> return quiz!!.option3
            option4 -> return quiz!!.option4
        }
        throw IllegalStateException("Invalid view provided")
    }

    override fun createPresenter() = quizPresenter

    override fun activeQuizIntent() = Observable.just(true)

    override fun submitQuizIntent() =
            RxView.clicks(submit)
                    .filter { selectedOptionValue.isNotEmpty() }
                    .map { selectedOptionValue }

    override fun nextQuizIntent() = RxView.clicks(next).map { true }

    override fun retakeQuizIntent() = RxView.clicks(retake).map { true }

    override fun timedOutQuizIntent() = timedOutSubject

    override fun render(viewModel: QuizViewModel) {
        Timber.i("render: $viewModel")
        when (viewModel.state) {
            QuizViewState.LOADING -> showQuizLoading()
            QuizViewState.ACTIVE -> showQuizState(viewModel)
            QuizViewState.ANSWERED -> showQuizAnswered(viewModel)
            QuizViewState.TIMED_OUT -> showTimedOut()
        }
    }

    private fun showQuizLoading() {
        submit.visibility = View.INVISIBLE
        next.visibility = View.INVISIBLE
        retake.visibility = View.INVISIBLE
        message.text = "Loading Quiz..."
        message.setTextColor(resources.getColor(R.color.success))
    }

    private fun showQuizState(viewModel: QuizViewModel) {
        if (viewModel.quiz == null) throw IllegalStateException("Quiz shouldn't be null in QuizViewState.ACTIVE state")
        quiz = viewModel.quiz
        question.text = viewModel.quiz.question
        picasso.load(viewModel.quiz.option1).into(option1)
        picasso.load(viewModel.quiz.option2).into(option2)
        picasso.load(viewModel.quiz.option3).into(option3)
        picasso.load(viewModel.quiz.option4).into(option4)

        clearOptionsSelection()
        submit.visibility = View.VISIBLE
        next.visibility = View.INVISIBLE
        retake.visibility = View.INVISIBLE
        message.text = ""
        message.setTextColor(resources.getColor(R.color.success))

        if (isTimeRemainingToAnswer(viewModel.quiz.totalTimeToAnswerInSeconds, viewModel.startTime))
            scheduleTimeOut(viewModel.quiz.totalTimeToAnswerInSeconds, viewModel.startTime)
    }

    private fun isTimeRemainingToAnswer(totalTime: Int, quizStartTime: Long): Boolean {
        val elapsedTimeInSeconds = (Calendar.getInstance().timeInMillis - quizStartTime) / 1000
        return elapsedTimeInSeconds < totalTime
    }

    private fun showQuizAnswered(viewModel: QuizViewModel) {
        val isCorrectOption = viewModel.answeredOption!!.isCorrect
        showMessage(isCorrectOption)
        selectOption(viewToSelect(viewModel.quiz!!, viewModel.answeredOption), isCorrectOption)
        submit.visibility = View.GONE
        next.visibility = View.VISIBLE
        retake.visibility = View.VISIBLE

        disposeTimeoutSubscription()
    }

    private fun showTimedOut() {
        submit.visibility = View.GONE
        next.visibility = View.VISIBLE
        retake.visibility = View.VISIBLE
        message.text = "Timed Out"
        message.setTextColor(resources.getColor(R.color.error))
    }

    private fun showMessage(isCorrect: Boolean) {
        if (isCorrect) {
            message.text = "Correct Answer"
            message.setTextColor(resources.getColor(R.color.success))
        } else {
            message.text = "Incorrect Answer"
            message.setTextColor(resources.getColor(R.color.error))
        }
    }

    private fun selectOption(optionToSelect: View) {
        clearOptionsSelection()
        optionToSelect.setBackgroundColor(resources.getColor(R.color.selection))
    }

    private fun selectOption(optionToSelect: View, isCorrect: Boolean) {
        clearOptionsSelection()
        optionToSelect.setBackgroundColor(resources.getColor(if (isCorrect) R.color.success else R.color.error))
    }

    private fun viewToSelect(quiz: Quiz, answeredOption: AnsweredOption): View {
        when (answeredOption.option) {
            quiz.option1 -> return option1
            quiz.option2 -> return option2
            quiz.option3 -> return option3
            quiz.option4 -> return option4
        }
        return option1
    }

    private fun clearOptionsSelection() {
        selectedOptionValue = ""
        option1.setBackgroundColor(Color.TRANSPARENT)
        option2.setBackgroundColor(Color.TRANSPARENT)
        option3.setBackgroundColor(Color.TRANSPARENT)
        option4.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun scheduleTimeOut(totalTime: Int, quizStartTime: Long) {
        val elapsedTimeInSeconds = (Calendar.getInstance().timeInMillis - quizStartTime) / 1000
        disposeTimeoutSubscription()
        timeoutSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .take(totalTime - elapsedTimeInSeconds + 1)
                .map { remainingTime(totalTime, it, elapsedTimeInSeconds) }
                .doOnComplete { timedOutSubject.onNext(true) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { showRemainingTime(it) }
    }

    private fun disposeTimeoutSubscription() {
        if (timeoutSubscription != null && !timeoutSubscription!!.isDisposed)
            timeoutSubscription!!.dispose()
    }

    private fun remainingTime(totalTime: Int, secondsElapsedAfterTimerStarted: Long, quizElapsedTimeInSeconds: Long) =
            totalTime - secondsElapsedAfterTimerStarted - quizElapsedTimeInSeconds

    private fun showRemainingTime(it: Long) {
        if (isAdded)
            message.text = it.toString()
    }
}