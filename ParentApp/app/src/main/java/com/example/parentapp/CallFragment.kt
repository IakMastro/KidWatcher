package com.example.parentapp

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CallFragment : Fragment() {
    private lateinit var dbHandler: DBHandler
    private lateinit var list: ArrayList<Call>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = DBHandler("CALL")

        val thread = Runnable {
            list = dbHandler.getCallList()
            Log.d("Firebase", list.toString())

            val recyclerView: RecyclerView? = view?.findViewById(R.id.call_recycler_view)
            val callAdapter = CallAdapter(list)
            recyclerView?.adapter = callAdapter
            recyclerView?.layoutManager = LinearLayoutManager(context)
        }

        val handler = Handler()
        handler.postDelayed(thread, 1000)
    }
}