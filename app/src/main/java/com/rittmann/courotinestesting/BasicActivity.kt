package com.rittmann.courotinestesting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.layout_test_1.*
import kotlinx.android.synthetic.main.layout_test_2.*
import kotlinx.android.synthetic.main.layout_test_3.*
import kotlinx.android.synthetic.main.layout_test_4.*
import kotlinx.android.synthetic.main.layout_test_5.*
import kotlinx.android.synthetic.main.layout_test_6.*
import kotlinx.android.synthetic.main.layout_test_7.*
import kotlinx.coroutines.*

class BasicActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        globalScopeCase()
        runBlockingCase()
        jobJoinCase()
        jobWithoutJoinCase()
        coroutineScopeInsideRunBlocking()
        multiplesCoroutinesWithSuspendFunction()
        finishTheGlobalScopeInsideRunBlocking()
    }

    private fun globalScopeCase() {
        buttonTestOne.setOnClickListener {
            loading()
            var text = ""

            GlobalScope.launch {
                val millis = 1000L
                delay(millis)
                text += "\nSecond called after $millis"

                runOnUiThread {
                    textTestOne.text = text
                    hideLoading()
                }
            }

            text += "First called using GlobalScope.launch"

            textTestOne.text = text
        }
    }

    private fun runBlockingCase() {
        buttonTestTwo.setOnClickListener {
            loading()
            var text = ""

            runBlocking {
                val millis = 1000L
                delay(millis)
                text += "Second called after $millis"

                runOnUiThread {
                    textTestTwo.text = text
                    hideLoading()
                }
            }

            text += "\nFirst called using runBlocking"

            textTestTwo.text = text
        }
    }

    private fun jobJoinCase() {
        buttonTestThree.setOnClickListener {
            loading()
            var text = ""

            GlobalScope.launch {
                val job = GlobalScope.launch {
                    val millis = 1000L
                    delay(millis)
                    text += "\nSecond called after $millis"

                    runOnUiThread {
                        textTestThree.text = text
                    }
                }

                text += "First called and wait with job.join"

                runOnUiThread {
                    textTestThree.text = text
                }

                job.join()

                text += "\nThird called after job.join"

                runOnUiThread {
                    textTestThree.text = text
                    hideLoading()
                }
            }
        }
    }

    private fun jobWithoutJoinCase() {
        buttonTestFour.setOnClickListener {
            loading()
            var text = ""

            GlobalScope.launch {
                val millis = 1000L
                delay(millis)
                text += "\nSecond called after $millis"

                runOnUiThread {
                    textTestFour.text = text
                }
            }

            text += "First called and wait without job.join"

            textTestFour.text = text

            text += "\nThird called"

            textTestFour.text = text
            hideLoading()
        }
    }

    private fun coroutineScopeInsideRunBlocking() {
        buttonTestFive.setOnClickListener {
            loading()
            var text = ""

            runBlocking {
                launch {
                    val delay = 500L
                    delay(delay)
                    text += "\nSecond called, task from runBlocking after $delay"
                }

                coroutineScope {
                    launch {
                        val delay = 1000L
                        delay(delay)
                        text += "\nThird called, Task from nested launch after $delay"
                    }

                    val delay = 300L
                    delay(delay)
                    text += "First called, task from coroutine scope after $delay"
                }

                text += "\nCoroutine scope is over"

                runOnUiThread {
                    textTestFive.text = text
                    hideLoading()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun multiplesCoroutinesWithSuspendFunction() {
        buttonTestSix.setOnClickListener {
            loading()
            GlobalScope.launch {
                val job = GlobalScope.launch {
                    val delay = 1000L

                    runOnUiThread {
                        textTestSix.text =
                            "Creating a lot of coroutines to show one dot each after $delay\n"
                    }

                    repeat(100) {
                        launch {
                            showDot(delay)
                        }
                    }
                }

                job.join()

                runOnUiThread {
                    hideLoading()

                    val t = textTestSix.text.toString()
                    textTestSix.text = "$t\nAfter join the task is finished"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun showDot(delay: Long) {
        delay(delay)
        runOnUiThread {
            val t = textTestSix.text.toString()
            textTestSix.text = "$t."
        }
    }

    private fun finishTheGlobalScopeInsideRunBlocking() {
        buttonTestSeven.setOnClickListener {
            loading()

            val repeatFor = 15
            val delay = 500L

            var text = "I'll repeat for $repeatFor times"
            textTestSeven.text = text

            runBlocking {

                GlobalScope.launch {

                    repeat(repeatFor) { i ->
                        text += "\nRunning by $i time(s) after $delay"

                        runOnUiThread {
                            textTestSeven.text = text
                        }

                        delay(500L)
                    }

                    text += "\nFinishing GlobalScope"

                    runOnUiThread {
                        textTestSeven.text = text
                        hideLoading()
                    }
                }

                val allDelay = 1300L
                delay(allDelay)

                text += "\nFinish runBlocking after $allDelay"

                runOnUiThread {
                    textTestSeven.text = text
                }
            }
        }
    }

    companion object {
        fun getIntentBaseActivity(context: Context) = Intent(context, BasicActivity::class.java)
    }
}
