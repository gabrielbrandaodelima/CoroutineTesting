package com.rittmann.courotinestesting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.layout_composing_suspend_functions_1.*
import kotlinx.android.synthetic.main.layout_composing_suspend_functions_2.*
import kotlinx.android.synthetic.main.layout_composing_suspend_functions_3.*
import kotlinx.android.synthetic.main.layout_composing_suspend_functions_4.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class ComposingSuspendFunctionsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_composing_suspend_functions)

        executeSuspendFunctionsInSequence()
        executeSuspendFunctionsInCocurranceUsingAsync()
        executeSuspendFunctionsInGlobalScopeAsync()
        propagatingExceptionToHierarchy()
    }

    private fun executeSuspendFunctionsInSequence() {
        buttonComposingSuspendFunctionsOne.setOnClickListener {
            loading()

            var text = "I'll sum two numbers"

            GlobalScope.launch {

                val time = measureTimeMillis {
                    val one = doSomethingOne()

                    text += "\nOne was executed"

                    runOnUiThread {
                        textComposingSuspendFunctionsOne.text = text
                    }

                    val two = doSomethingTwo()

                    text += "\nTwo was executed"

                    runOnUiThread {
                        textComposingSuspendFunctionsOne.text = text
                    }

                    text += "\nThe answer is ${one + two}"
                }

                text += "\nCompleted in $time ms"

                runOnUiThread {
                    textComposingSuspendFunctionsOne.text = text
                    hideLoading()
                }
            }
        }
    }

    private fun executeSuspendFunctionsInCocurranceUsingAsync() {
        buttonComposingSuspendFunctionsTwo.setOnClickListener {
            loading()

            var text = "I'll sum two numbers"

            GlobalScope.launch {

                val time = measureTimeMillis {
                    val one = async {
                        doSomethingOne()
                    }

                    val two = async {
                        doSomethingTwo()
                    }

                    text += "\nThe answer is ${one.await() + two.await()}"
                }

                text += "\nCompleted in $time ms"

                runOnUiThread {
                    textComposingSuspendFunctionsTwo.text = text
                    hideLoading()
                }
            }
        }
    }

    private fun executeSuspendFunctionsInGlobalScopeAsync() {
        buttonComposingSuspendFunctionsThree.setOnClickListener {
            loading()

            var text = "I'll sum two numbers"

            GlobalScope.launch {

                val time = measureTimeMillis {
                    val one = doSomethingOneAsync()

                    val two = doSomethingTwoAsync()

                    text += "\nThe answer is ${one.await() + two.await()}"
                }

                text += "\nCompleted in $time ms"

                runOnUiThread {
                    textComposingSuspendFunctionsThree.text = text
                    hideLoading()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun propagatingExceptionToHierarchy() {
        buttonComposingSuspendFunctionsFour.setOnClickListener {
            loading()

            GlobalScope.launch {
                try {
                    failedConcurrentSum()
                } catch (e: ArithmeticException) {
                    runOnUiThread {
                        val t = textComposingSuspendFunctionsFour.text.toString()
                        textComposingSuspendFunctionsFour.text =
                            "$t\nComputation failed with ArithmeticException"
                        hideLoading()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one = async {
            try {
                delay(Long.MAX_VALUE) // Emulates very long computation
                42
            } finally {
                runOnUiThread {
                    val t = textComposingSuspendFunctionsFour.text.toString()
                    textComposingSuspendFunctionsFour.text =
                        "$t\nFirst child was cancelled"
                }
            }
        }
        val two = async<Int> {
            runOnUiThread {
                textComposingSuspendFunctionsFour.text =
                    "Second child throws an exception"
            }
            throw ArithmeticException()
        }

        one.await() + two.await()
    }

    private suspend fun doSomethingOne(): Int {
        delay(1000L)
        return 10
    }

    private suspend fun doSomethingTwo(): Int {
        delay(1000L)
        return 15
    }

    private fun doSomethingOneAsync() = GlobalScope.async {
        doSomethingOne()
    }

    private suspend fun doSomethingTwoAsync() = GlobalScope.async {
        doSomethingTwo()
    }

    companion object {
        fun getIntentComposingSuspendFunctions(context: Context) =
            Intent(context, ComposingSuspendFunctionsActivity::class.java)
    }
}
