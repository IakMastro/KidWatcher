package com.example.kidwatcher;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class DBHandler {
    String url = "https://kid-watcher-8403f-default-rtdb.europe-west1.firebasedatabase.app/";
    private FirebaseDatabase database;
    private DatabaseReference logs;

    public DBHandler() {
        database = FirebaseDatabase.getInstance(url);
        database.setPersistenceEnabled(true);
        logs = database.getReference();
        logs.keepSynced(true);
    }

    public void keepLogs(String operation, String action,
                         String date, String message, String number) {
        if (operation.equals("SMS")) {
            logs.child(operation).child(number).child(date).child(action).setValue(message);
        }
    }
}
