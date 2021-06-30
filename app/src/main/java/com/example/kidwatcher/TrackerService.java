package com.example.kidwatcher;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackerService extends Service {
    private DBHandler databaseHandler;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseHandler = new DBHandler();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter("sms_sender"));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter("phone"));

        return super.onStartCommand(intent, flags, startId);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String operation = intent.getStringExtra("operation");
            String date = formatter.format(new Date());
            String message = intent.getStringExtra("message");
            String number = intent.getStringExtra("number");

            if (operation.equals("SMS")) {
                databaseHandler.keepSMSLogs("incoming", date, message, number);
                // TODO: "sent" sms
            } else if (operation.equals("Phone")) {
                databaseHandler.keepPhoneLogs("type", date, message, number);
                // TODO: "out-coming" call
            }
        }
    };
}
