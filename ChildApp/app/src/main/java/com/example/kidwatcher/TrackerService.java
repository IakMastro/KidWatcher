package com.example.kidwatcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackerService extends Service implements LocationListener
{
	private DBHandler databaseHandler;
	@SuppressLint("SimpleDateFormat")
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private LocationManager locationManager;
	private Location location;

	@Override
	public void onCreate()
	{
		super.onCreate();
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new smsOutgoing(new Handler()));
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		databaseHandler = new DBHandler();

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("sms_sender"));
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("phone"));
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("gps"));

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//		criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
//		criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(false);
//		criteria.setSpeedRequired(false);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
		Log.d("***PROVIDER", "***\nABOVE GETBESTPROVIDER");
		String provider = locationManager.getBestProvider(criteria, true);
		Log.d("***OUTIF", "***\nOUTSIDE IF PROVIDER NOT NULL");
		if (provider != null)
		{
			Log.d("***IF", "***\nIN IF PROVIDER NOT NULL");
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			{
				Toast.makeText(this, "You need to give the location permissions...", Toast.LENGTH_LONG).show();
			}
			location = locationManager.getLastKnownLocation(provider);
			onLocationChanged(location);
			locationManager.requestLocationUpdates(provider, 5000, 0f, this); // 300.000
			//			Log.d("test", "Latitude: " + location.getLatitude() + "\tLongitude: " + location.getLongitude());
			Log.d("***LATLON", "***\nLAT = " + location.getLatitude() + "\nLON = " + location.getLongitude());
		}

		return START_STICKY;
	}

	BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String operation = intent.getStringExtra("operation");
			String date = formatter.format(new Date());
			String message = intent.getStringExtra("message");
			String number = intent.getStringExtra("number");
			String status = intent.getStringExtra("status");
			String duration = intent.getStringExtra("duration");
			String lat = intent.getStringExtra("latitude");
			String lon = intent.getStringExtra("longitude");

			if (operation.equals("SMS"))
			{
				Log.d("***TRACKERSERVICE", "***\nTRACKERSERVICE");
				databaseHandler.keepSMSLogs(date, status, number, message);
			}
			else if (operation.equals("Phone"))
			{
				databaseHandler.keepPhoneLogs(date, status, number, duration);
			}
			else if (operation.equals("GPS"))
			{
				databaseHandler.keepLocationLogs(lat, lon);
			}
		}
	};

	@Override
	public void onLocationChanged(@NonNull Location location)
	{
		this.location = location;
	}

	class smsOutgoing extends ContentObserver
	{
		private String previousSms_id;
		private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public smsOutgoing(Handler handler)
		{
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange)
		{
			super.onChange(selfChange);
			Uri smsURI = Uri.parse("content://sms");
			Cursor curs = getContentResolver().query(smsURI, null, null, null, null);
			if (curs.moveToNext())
			{
				String id = curs.getString(curs.getColumnIndex("_id"));
				String type = curs.getString(curs.getColumnIndex("type"));
				String address = curs.getString(curs.getColumnIndex("address"));
				String body = curs.getString(curs.getColumnIndex("body"));
				//				Log.d("***UP", "***\npreviousid = " + previousSms_id + "\nid = " + id + "\ntype = " + type + "\naddress = " + address + "\nbody = " + body);
				if (type.equals("2"))
				{
					//					Log.d("***IN IF 1", "***\nIN IF 1");
					if (!id.equals(previousSms_id))
					{
						//						Log.d("***IN IF 2", "***\nIN IF 2");
						previousSms_id = id;
						String date = formatter.format(new Date());
						databaseHandler.keepSMSLogs(date, "outgoing", address, body);
					}
				}
			}
		}
	}
}
