package com.shivamsingh.groceryquiz.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.ButterKnife
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseFragment<V : MvpView, P : MviPresenter<V, *>> : MviFragment<V, P>() , HasSupportFragmentInjector {
    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    abstract fun layoutRes(): Int

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    open fun showLoading() {
    }

    open fun hideLoading() {
    }

    open fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun showMessage(res: Int) {
        Toast.makeText(context, res, Toast.LENGTH_LONG).show()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return childFragmentInjector
    }
}
