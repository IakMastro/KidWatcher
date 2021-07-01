package com.example.kidwatcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackerService extends Service implements LocationListener {
    private DBHandler databaseHandler;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LocationManager locationManager;
    private Location location;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseHandler = new DBHandler();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("sms_sender"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("phone"));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setSpeedRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You need to give the location permissions...",
                        Toast.LENGTH_LONG).show();
            }
            location = locationManager.getLastKnownLocation(provider);
            onLocationChanged(location);
            locationManager.requestLocationUpdates(provider, 10000, 2.5f, this); // 300.000
            Log.d("test", "Latitude: " + location.getLatitude() + "\tLongitude: " + location.getLongitude());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String operation = intent.getStringExtra("operation");
            String date = formatter.format(new Date());
            String message = intent.getStringExtra("message");
            String number = intent.getStringExtra("number");
            String status = intent.getStringExtra("status");
            String duration = intent.getStringExtra("duration");

            if (operation.equals("SMS")) {
                databaseHandler.keepSMSLogs("incoming", date, message, number);
                // TODO: "sent" sms
            } else if (operation.equals("Phone")) {
                databaseHandler.keepPhoneLogs(date, status, number, duration);
            }
        }
    };

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
    }
}
