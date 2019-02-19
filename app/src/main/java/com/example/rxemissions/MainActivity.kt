package com.example.rxemissions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pipe = ExhaustPipe<String>()

        buttonSingle.setOnClickListener {
            pipe.addSingleItemToPipe(generate())
        }

        button10.setOnClickListener {
            val list = mutableListOf<String>()
            (0..10).forEach {
                list.add(generate())
            }
            pipe.addMultipleItemsToPipe(list)
        }

        button100.setOnClickListener {
            val list = mutableListOf<String>()
            (0..100).forEach {
                list.add(generate())
            }
            pipe.addMultipleItemsToPipe(list)
        }

        buttonListen.setOnClickListener {
            pipe.listToPipeHole().subscribe {
                Log.d("Listener", it.toString())
            }
        }

        pipe.listToPipeHole().subscribe {
            Log.d("Listener", it.toString())
        }
    }

    private fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
