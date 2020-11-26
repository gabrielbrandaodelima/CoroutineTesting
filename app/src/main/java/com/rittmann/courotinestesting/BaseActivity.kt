package com.rittmann.courotinestesting

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected fun loading() {
        findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
    }

    protected  fun hideLoading() {
        findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
    }
}