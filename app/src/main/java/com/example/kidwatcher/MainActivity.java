package com.example.kidwatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private DBHandler databaseHandler;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int PERMISSIONS_CHECK = 1;
        String[] PERMISSIONS = {
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG
        };
        checkPermissions(this, PERMISSIONS, PERMISSIONS_CHECK);

        databaseHandler = new DBHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter("sms_sender"));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter("phone"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void checkPermissions(MainActivity mainActivity, String[] permissions, int permissions_check){
        if (!permissionExists(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, permissions_check);
        }
    }

    public static boolean permissionExists(Context context, String... permissions){
        if (context != null && permissions != null){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    BroadcastReceiver receiver =  new BroadcastReceiver() {
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