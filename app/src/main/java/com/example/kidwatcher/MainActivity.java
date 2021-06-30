package com.example.kidwatcher;

import androidx.annotation.RequiresApi;
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
import android.os.Build;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
	@RequiresApi(api = Build.VERSION_CODES.P)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int PERMISSIONS_CHECK = 1;
		String[] PERMISSIONS = {
				Manifest.permission.RECEIVE_BOOT_COMPLETED,
				Manifest.permission.RECEIVE_SMS,
				Manifest.permission.READ_SMS,
				Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.READ_CALL_LOG,
				Manifest.permission.FOREGROUND_SERVICE
		};
		checkPermissions(PERMISSIONS, PERMISSIONS_CHECK);

		startService(new Intent(this, TrackerService.class));
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}

	public void checkPermissions(String[] permissions, int permissions_check)
	{
		if (!permissionExists(this, permissions))
		{
			ActivityCompat.requestPermissions(this, permissions, permissions_check);
		}
	}

	public static boolean permissionExists(Context context, String... permissions)
	{
		if (context != null && permissions != null)
		{
			for (String permission : permissions)
			{
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
				{
					return false;
				}
			}
		}
		return true;
	}
}