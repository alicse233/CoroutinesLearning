package com.example.croutineslearning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.*
/*
* Author - Ali
* Date - 19 Oct 2021
* */

class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Simple coroutine dispatch which will simulate
//        network call of 6 sec in total delay
        GlobalScope.launch {
            delay(30)
            doNetworkCall()
            doNetworkCall2()
            Log.d(TAG, "Hi from global scoped coroutine and thread is ${Thread.currentThread().name}")
        }

//        we can pass dispatchers to launch for defining
//        in which context or thread this coroutine will trigger
        GlobalScope.launch(Dispatchers.Main) {
//            for some thing that can reflect in main thread or UI thread because you need to update UI
        }

        GlobalScope.launch(Dispatchers.IO) {
//            for some thing that you wanna execute related to data operations in database
        }

        GlobalScope.launch(Dispatchers.Unconfined) {
//            for running code in current thread and not specifying any other thread
        }

        GlobalScope.launch(Dispatchers.Default) {
//            for running some heavy calculations like sorting 10000 item list
        }

        GlobalScope.launch(newSingleThreadContext("MyThread")) {
//            if you want to make your own new thread and execute lines of code within that
        }


//        Now why these contexts are useful?
//        They are helpful in switching context while doing some operations.
//        Like you make network request in background thread
//        Then you switch the context to main thread or context while network response is received
//        see below...

        GlobalScope.launch(Dispatchers.IO) {
            val response = fetchResponse()
            Log.d(TAG, "working in ${Thread.currentThread().name}")
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.tvText).text = response
                Log.d(TAG, "Updating text in ${Thread.currentThread().name}")
            }
        }

//        Now lets learn run blocking
//        If you want to do the delay function run in main thread
//        and actually want to block main thread then you
//        can achieve it by runblocking.
//        We usually use it in testing and also if you have some suspend
//        function and want it to run on main thread then you can go for this runblocking

        Log.d(TAG, "Before run blocking")
        runBlocking {
            Log.d(TAG, "Start run blocking")

//            Now here we can do launch {} to do some stuff asyncronusly
//            and as we are in coroutine scope we can call this suspend function
//            without the global scope
//            also here I'm adding two launchs and both of them will execute simultaneously

            launch(Dispatchers.IO) {
                delay(1000L)
                Log.d(TAG, "From launch 1 in runblocling")
            }

            launch(Dispatchers.IO) {
                delay(1000L)
                Log.d(TAG, "From launch 2 in runblocling")
            }

            delay(3000L)
            Log.d(TAG, "After delay run blocking")
            Log.d(TAG, "printing this in run blocking in thread - ${Thread.currentThread().name}")
        }


//        Now practicing the job cancellation and join
//        A coroutine gives you a job you can store it reused it after some time
        val job = GlobalScope.launch {
            Log.d(TAG, "Starting long running calculation..")

//            Also making some making it time specific like below

            withTimeout(3000L) {

                for (i in 30..40) {
//                isActive is compulsory for stopping coroutine
//                if we don't put it here then coroutine will not
//                stop because it is so busy in calculation
                    if (isActive) {
                        Log.d(TAG, "Result of $i : ${fin(i)}")
                    }
                }

            }

            Log.d(TAG, "Ending long running calculation..")
        }

        runBlocking {
            delay(100L)
            job.cancel()
            Log.d(TAG, "Job cancelled!")
        }

        Log.d(TAG, "After run blocking is finished")

        Log.d(TAG, "Main thread is ${Thread.currentThread().name}")
    }

    private suspend fun fetchResponse(): String {
        delay(3000L)
        return "Here is response"
    }

    private suspend fun doNetworkCall() {
        delay(3000L)
    }

    private suspend fun doNetworkCall2() {
        delay(3000L)
    }

    private fun fin(i:Int): Long {
        return if(i == 0) 0
        else if(i == 1) 1
        else fin(i-1) + fin(i-2)
    }
}