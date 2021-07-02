package com.example.kidwatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.time.Duration;
import java.time.Instant;

public class StartUpReceiver extends BroadcastReceiver
{
	Context context;
	private static String previousStatus = "idle";
	private static boolean justRang = false;
	private static Instant start = Instant.now();
	private static Instant end = Instant.now();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.context = context;
		Log.d("***STARTUPRECEIVER", "***\nSTARTUPRECEIVER");

		String action = intent.getAction();
		if (action.equals("android.provider.Telephony.SMS_RECEIVED")) doSms(intent);
		else if (action.equals("android.intent.action.PHONE_STATE")) doPhone(intent);
	}

	void doSms(Intent intent)
	{
		Bundle bundle = intent.getExtras();
		SmsMessage[] messages = null;

		if (bundle != null)
		{
			Object[] pdus = (Object[]) bundle.get("pdus");
			messages = new SmsMessage[pdus.length];

			int i = 0;
			for (SmsMessage message : messages)
			{
				message = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));

				String smsSender = message.getOriginatingAddress();
				String smsBody = message.getMessageBody();

				i++;
				logSMS(smsSender, "incoming", smsBody);
			}
		}
	}

	void doPhone(Intent intent)
	{
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		String caller = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
		{
			if (caller != null)
			{
				justRang = true;
				previousStatus = "ringing";
			}
		}
		else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{
			if (caller != null)
			{
				start = Instant.now();
				previousStatus = "offhook";
			}
		}
		else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
		{
			if (caller != null)
			{
				if (previousStatus.equals("offhook"))
				{
					end = Instant.now();
					int timeElapsed = (int) (Duration.between(start, end).toMillis() / 1000);
					String callDuration = "";

					if (timeElapsed >= 86400)
					{
						int days = timeElapsed / 86400;
						int hours = (timeElapsed % 86400) / 3600;
						int minutes = ((timeElapsed % 86400) % 3600) / 60;
						int seconds = ((timeElapsed % 86400) % 3600) % 60;
						callDuration = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
					}
					else if (timeElapsed >= 3600)
					{
						int hours = timeElapsed / 3600;
						int minutes = (timeElapsed % 3600) / 60;
						int seconds = (timeElapsed % 3600) % 60;
						callDuration = hours + "h " + minutes + "m " + seconds + "s";
					}
					else if (timeElapsed >= 60)
					{
						int minutes = timeElapsed / 60;
						int seconds = timeElapsed % 60;
						callDuration = minutes + "m " + seconds + "s";
					}
					else
					{
						callDuration = timeElapsed + "s";
					}

					if (justRang)
					{
						justRang = false;
						logPhone(caller, "incoming", callDuration);
					}
					else
					{
						logPhone(caller, "outgoing", callDuration);
					}
				}
				else if (previousStatus.equals("ringing"))
				{
					justRang = false;
					logPhone(caller, "incoming", "unanswered");
				}
				previousStatus = "idle";
			}
		}
	}

	void logSMS(String number, String status, String message)
	{
		Intent intent = new Intent("sms_sender");
		intent.putExtra("operation", "SMS");
		intent.putExtra("number", number);
		intent.putExtra("status", status);
		intent.putExtra("message", message);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	void logPhone(String number, String status, String duration)
	{
		Intent intent = new Intent("phone");
		intent.putExtra("operation", "Phone");
		intent.putExtra("number", number);
		intent.putExtra("status", status);
		intent.putExtra("duration", duration);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	void logGps(String lat, String lon)
	{
		Intent intent = new Intent("gps");
		intent.putExtra("operation", "GPS");
		intent.putExtra("latitude", lat);
		intent.putExtra("longitude", lon);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}