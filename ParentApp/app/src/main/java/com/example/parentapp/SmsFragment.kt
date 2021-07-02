package com.example.parentapp

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SmsFragment : Fragment(R.layout.fragment_sms) {
    private lateinit var dbHandler: DBHandler
    private lateinit var list: ArrayList<SMS>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHandler = DBHandler("SMS")

        val thread = Runnable {
            list = dbHandler.getSMSList()
            Log.d("Firebase", list.toString())

            val recyclerView: RecyclerView? = view?.findViewById(R.id.sms_recycler_view)
            val smsAdapter = SMSAdapter(list)
            recyclerView?.adapter = smsAdapter
            recyclerView?.layoutManager = LinearLayoutManager(context)
        }

        val handler = Handler()
        handler.postDelayed(thread, 1000)
    }
}