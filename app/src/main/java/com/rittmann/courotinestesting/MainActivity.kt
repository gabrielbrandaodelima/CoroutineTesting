package com.rittmann.courotinestesting

import android.annotation.SuppressLint
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("SetTextI18n")
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openBasic.setOnClickListener {
            BasicActivity.getIntentBaseActivity(this@MainActivity).apply {
                startActivity(this)
            }
        }

        openCancellationAndTimeouts.setOnClickListener {
            CancellationAndTimeoutsActivity.getIntentCancellationAndTimeoutActivity(this@MainActivity)
                .apply {
                    startActivity(this)
                }
        }

        openComposingSuspendFunctions.setOnClickListener {
            ComposingSuspendFunctionsActivity.getIntentComposingSuspendFunctions(this@MainActivity)
                .apply {
                    startActivity(this)
                }
        }
    }
}
