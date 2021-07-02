package com.example.parentsapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                .add(R.id.fragment_container_view, SmsFragment::class.java, null).commit()
        }
    }

    fun showSMS(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, SmsFragment::class.java, null).commit()
    }
    fun showCalls(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, CallFragment::class.java, null).commit()
    }
    fun showGPS(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, MapsFragment::class.java, null).commit()
    }
}