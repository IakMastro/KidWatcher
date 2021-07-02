package com.example.kidwatcher;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity
{
	@RequiresApi(api = Build.VERSION_CODES.P)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		stopService(new Intent(this, TrackerService.class));
		setContentView(R.layout.activity_main);
		startService(new Intent(this, TrackerService.class));

		int PERMISSIONS_CHECK = 1;
		String[] PERMISSIONS = {
				Manifest.permission.RECEIVE_BOOT_COMPLETED,
				Manifest.permission.RECEIVE_SMS,
				Manifest.permission.READ_SMS,
				Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.READ_CALL_LOG,
				Manifest.permission.FOREGROUND_SERVICE,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.INTERNET,
				Manifest.permission.SEND_SMS,
				Manifest.permission.READ_PHONE_NUMBERS
		};
		checkPermissions(PERMISSIONS, PERMISSIONS_CHECK);

		//		spyButton.setOnClickListener(new View.OnClickListener()
		//		{
		//			@Override
		//			public void onClick(View v)
		//			{
		//
		//				PackageManager packageManager = getPackageManager();
		//				ComponentName componentName = new ComponentName(MainActivity.this, MainActivity.class);
		//				packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		//			}
		//		});
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		stopService(new Intent(this, TrackerService.class));
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