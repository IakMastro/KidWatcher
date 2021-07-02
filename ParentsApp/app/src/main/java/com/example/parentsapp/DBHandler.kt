package com.example.parentsapp

import android.util.Log
import com.google.firebase.database.*

class DBHandler(private val operation: String) {
    private var url = "https://kid-watcher-8403f-default-rtdb.europe-west1.firebasedatabase.app/"
    private var logs: DatabaseReference
    private var SMSList: ArrayList<SMS>
    private var callList: ArrayList<Call>
    private lateinit var gps: GPS

    init {
        val database = FirebaseDatabase.getInstance(url)
        logs = database.reference
        SMSList = ArrayList<SMS>()
        callList = ArrayList<Call>()

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (operation == "GPS") {
                    Log.d("Firebase", snapshot.child("latitude").value.toString())
                    Log.d("Firebase", snapshot.child("longitude").value.toString())
                    gps = GPS(
                        snapshot.child("latitude").value.toString(),
                        snapshot.child("longitude").value.toString()
                    )
                } else {
                    for (entry in snapshot.children) {
                        if (operation == "SMS")
                            SMSList.add(
                                SMS(
                                    entry.child("number").value.toString(),
                                    entry.child("message").value.toString(),
                                    entry.child("date").value.toString(),
                                    entry.child("type").value.toString()
                                )
                            )
                        else if (operation == "CALL")
                            callList.add(
                                Call(
                                    entry.child("number").value.toString(),
                                    entry.child("duration").value.toString(),
                                    entry.child("date").value.toString(),
                                    entry.child("type").value.toString()
                                )
                            )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        logs.child(operation).addListenerForSingleValueEvent(valueEventListener)
    }

    public fun getSMSList(): ArrayList<SMS> {
        return SMSList
    }

    fun getCallList(): ArrayList<Call> {
        return callList
    }

    fun getGPS(): GPS {
        return gps
    }
}