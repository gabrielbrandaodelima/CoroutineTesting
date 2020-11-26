package com.rittmann.courotinestesting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.layout_cancel_timeout_test_1.*
import kotlinx.android.synthetic.main.layout_cancel_timeout_test_2.*
import kotlinx.android.synthetic.main.layout_cancel_timeout_test_3.*
import kotlinx.android.synthetic.main.layout_cancel_timeout_test_4.*
import kotlinx.android.synthetic.main.layout_cancel_timeout_test_5.*
import kotlinx.coroutines.*

class CancellationAndTimeoutsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancellation_and_timeouts)

        cancellingJobAndJoin()
        tryCancelButTheCodeIsNotCancellable()
        makingTheProcessBeCancellable()
        createNewCourotineOnCancelledContext()
        stopWithTimeout()
    }

    private fun cancellingJobAndJoin() {
        buttonCancelTimeoutTestOne.setOnClickListener {
            loading()

            val repeatFor = 20

            var text = "I'll repeat for $repeatFor times"
            textCancelTimeoutTestOne.text = text

            GlobalScope.launch {
                val delay = 500L
                val job = launch {
                    repeat(repeatFor) { i ->
                        text += "\njob: I'm sleeping $i ... after $delay"

                        runOnUiThread {
                            textCancelTimeoutTestOne.text = text
                        }

                        delay(delay)
                    }
                }

                val delayAll = 1300L
                delay(delayAll)
                text += "\nmain: I'm tired of waiting for $delayAll!"

                runOnUiThread {
                    textCancelTimeoutTestOne.text = text
                }

                job.cancel()
                job.join()

                text += "\nmain: Now I can quit."
                runOnUiThread {
                    textCancelTimeoutTestOne.text = text
                    hideLoading()
                }
            }
        }
    }

    private fun tryCancelButTheCodeIsNotCancellable() {
        buttonCancelTimeoutTestTwo.setOnClickListener {
            loading()

            val repeatFor = 5

            var text = "I'll repeat for $repeatFor times"
            textCancelTimeoutTestTwo.text = text

            GlobalScope.launch {
                val startTime = System.currentTimeMillis()
                val delay = 500L
                val job = launch(Dispatchers.Default) {
                    var nextPrintTime = startTime
                    var i = 0

                    while (i < repeatFor) { // computation loop, just wastes CPU
                        if (System.currentTimeMillis() >= nextPrintTime) {
                            text += "\njob: I'm sleeping ${i++} ... after $delay"

                            runOnUiThread {
                                textCancelTimeoutTestTwo.text = text
                            }

                            nextPrintTime += delay
                        }
                    }
                }

                val delayAll = 1300L
                delay(delayAll)
                text += "\nmain: I'm tired of waiting for $delayAll! Requesting for cancel and join"

                runOnUiThread {
                    textCancelTimeoutTestTwo.text = text
                }

                job.cancelAndJoin()

                text += "\nmain: Now I can quit."
                runOnUiThread {
                    textCancelTimeoutTestTwo.text = text
                    hideLoading()
                }
            }
        }
    }

    private fun makingTheProcessBeCancellable() {
        buttonCancelTimeoutTestThree.setOnClickListener {
            loading()

            val repeatFor = 5

            var text = "I'll repeat for $repeatFor times"
            textCancelTimeoutTestThree.text = text

            GlobalScope.launch {
                val startTime = System.currentTimeMillis()
                val delay = 500L
                val job = launch(Dispatchers.Default) {
                    var nextPrintTime = startTime
                    var i = 0

                    while (isActive) { // computation loop, just wastes CPU
                        if (System.currentTimeMillis() >= nextPrintTime) {
                            text += "\njob: I'm sleeping ${i++} ... after $delay"

                            runOnUiThread {
                                textCancelTimeoutTestThree.text = text
                            }

                            nextPrintTime += delay
                        }
                    }
                }

                val delayAll = 1300L
                delay(delayAll)
                text += "\nmain: I'm tired of waiting for $delayAll! Requesting for cancel and join"

                runOnUiThread {
                    textCancelTimeoutTestThree.text = text
                }

                job.cancelAndJoin()

                text += "\nmain: Now I can quit."
                runOnUiThread {
                    textCancelTimeoutTestThree.text = text
                    hideLoading()
                }
            }
        }
    }

    private fun createNewCourotineOnCancelledContext() {
        buttonCancelTimeoutTestFour.setOnClickListener {
            loading()

            val repeatFor = 20

            var text = "I'll repeat for $repeatFor times"
            textCancelTimeoutTestFour.text = text

            GlobalScope.launch {
                val delay = 500L
                val job = launch {
                    try {
                        repeat(repeatFor) { i ->
                            text += "\njob: I'm sleeping $i ... after $delay"

                            runOnUiThread {
                                textCancelTimeoutTestFour.text = text
                            }

                            delay(delay)
                        }
                    } finally {
                        withContext(NonCancellable) {
                            text += "\njob: I'm running finally"

                            runOnUiThread {
                                textCancelTimeoutTestFour.text = text
                            }

                            delay(2000L)

                            text += "\njob: And I've just delayed for 2 sec because I'm non-cancellable"

                            runOnUiThread {
                                textCancelTimeoutTestFour.text = text
                            }
                        }
                    }
                }

                val delayAll = 1300L
                delay(delayAll)
                text += "\nmain: I'm tired of waiting for $delayAll!"

                runOnUiThread {
                    textCancelTimeoutTestFour.text = text
                }

                job.cancelAndJoin()

                text += "\nmain: Now I can quit."
                runOnUiThread {
                    textCancelTimeoutTestFour.text = text
                    hideLoading()
                }
            }
        }
    }

    private fun stopWithTimeout() {
        buttonCancelTimeoutTestFive.setOnClickListener {
            loading()

            val repeatFor = 20

            var text = "I'll repeat for $repeatFor times"
            textCancelTimeoutTestFive.text = text

            GlobalScope.launch {
                val delay = 500L
                val timeout = 1300L
                withTimeoutOrNull(timeout) {
                    repeat(repeatFor) { i ->
                        text += "\njob: I'm sleeping $i ... after $delay"

                        runOnUiThread {
                            textCancelTimeoutTestFive.text = text
                        }

                        delay(delay)
                    }
                }

                text += "\nmain: I'm stopped because of timeout of $timeout!"

                runOnUiThread {
                    textCancelTimeoutTestFive.text = text
                    hideLoading()
                }
            }
        }
    }

    companion object {
        fun getIntentCancellationAndTimeoutActivity(context: Context) =
            Intent(context, CancellationAndTimeoutsActivity::class.java)
    }
}
