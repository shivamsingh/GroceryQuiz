package com.shivamsingh.groceryquiz.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import butterknife.BindView
import com.shivamsingh.groceryquiz.R
import com.shivamsingh.groceryquiz.ui.base.BaseActivity
import com.shivamsingh.groceryquiz.ui.quiz.QuizFragment
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var quizFragment: QuizFragment

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    override fun layoutRes() = R.layout.main

    override fun afterViews(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        setScreen(R.id.frame, quizFragment)
    }
}