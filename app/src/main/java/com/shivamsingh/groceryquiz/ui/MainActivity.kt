package com.shivamsingh.groceryquiz.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import butterknife.BindView
import com.shivamsingh.groceryquiz.R
import com.shivamsingh.groceryquiz.ui.base.BaseActivity

class MainActivity : BaseActivity() {
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    override fun layoutRes() = R.layout.main

    override fun afterViews(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
    }
}