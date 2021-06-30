package com.example.kidwatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.time.Duration;
import java.time.Instant;

public class StartUpReceiver extends BroadcastReceiver
{
	Context context;
	private static String previousStatus = "idle";
//	private static Instant start = Instant.now();
//	private static Instant end = Instant.now();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.context = context;

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
				logSMS(smsSender, smsBody);
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
				previousStatus = "ringing";
			}
		}
		else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{
			if (caller != null)
			{
//				start = Instant.now();
				if (previousStatus.equals("idle"))
				{
					logPhone(caller, "outgoing");
				}
				else if (previousStatus.equals("ringing"))
				{
					logPhone(caller, "incoming");
				}
				previousStatus = "offhook";
			}
		}
		else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
		{
			if (caller != null)
			{
//				end = Instant.now();
//				Duration timeElapsed = Duration.between(start, end);
//				if (){
//					logPhone(caller, "time elapsed: " + timeElapsed.getSeconds());
//				}
				previousStatus = "idle";
			}
		}
	}

	void logSMS(String number, String message)
	{
		Intent intent = new Intent("sms_sender");
		intent.putExtra("operation", "SMS");
		intent.putExtra("number", number);
		intent.putExtra("message", message);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	void logPhone(String number, String state)
	{
		Intent intent = new Intent("phone");
		intent.putExtra("operation", "Phone");
		intent.putExtra("number", number);
		intent.putExtra("message", state);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
