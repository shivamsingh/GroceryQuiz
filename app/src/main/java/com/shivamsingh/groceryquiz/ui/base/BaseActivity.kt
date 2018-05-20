package com.shivamsingh.groceryquiz.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes())
        ButterKnife.bind(this)
        afterViews(savedInstanceState)
    }

    abstract fun layoutRes(): Int

    abstract fun afterViews(savedInstanceState: Bundle?)

    fun setScreen(containerId: Int, fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(containerId, fragment, fragment.javaClass.simpleName)
                .commit()
    }
}