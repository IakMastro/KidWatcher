package com.example.kidwatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class StartUpReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String action = intent.getAction();

        if (action.equals("android.provider.Telephony.SMS_RECEIVED"))
            doSms(intent);
    }

    void doSms(Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            int i = 0;
            for (SmsMessage message : messages) {
                message = SmsMessage.createFromPdu((byte[]) pdus[i],
                        bundle.getString("format"));

                String smsSender = message.getOriginatingAddress();
                String smsBody = message.getMessageBody();

                i++;
                logMessage("SMS", smsSender, smsBody);
            }
        }
    }

    void logMessage(String operation, String number, String message) {
        Intent intent = new Intent("sms_sender");
        intent.putExtra("operation", operation);
        intent.putExtra("number", number);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
