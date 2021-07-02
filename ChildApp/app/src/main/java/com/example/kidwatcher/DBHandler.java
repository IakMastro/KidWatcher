package com.example.kidwatcher;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBHandler
{
	String url = "https://kid-watcher-8403f-default-rtdb.europe-west1.firebasedatabase.app/";
	private final DatabaseReference logs;

	public DBHandler()
	{
		FirebaseDatabase database = FirebaseDatabase.getInstance(url);
		database.setPersistenceEnabled(true);
		logs = database.getReference();
		logs.keepSynced(true);
	}

	public void keepSMSLogs(String date, String status, String number, String message)
	{
		logs.child("SMS").child(number).child(date).push().child(status).setValue(message);
	}

	public void keepPhoneLogs(String date, String status, String number, String duration)
	{
		String callId = logs.child("CALL").child(number).child(date).push().getKey();
		logs.child("CALL").child(number).child(date).child(callId).child("type").setValue(status);
		logs.child("CALL").child(number).child(date).child(callId).child("duration").setValue(duration);
	}

	public void keepLocationLogs(String lat, String lon)
	{
		logs.child("GPS").child("latitude").setValue(lat);
		logs.child("GPS").child("longitude").setValue(lon);
	}
}
