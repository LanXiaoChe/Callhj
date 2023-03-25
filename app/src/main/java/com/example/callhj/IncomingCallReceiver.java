package com.example.callhj;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class IncomingCallReceiver extends BroadcastReceiver {
    public static final String ACTION_INCOMING_CALL = "com.example.callhj.ACTION_INCOMING_CALL";
    public static final String EXTRA_INCOMING_NUMBER = "com.example.callhj.EXTRA_INCOMING_NUMBER";
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Log.d("IncomingCallReceiver", "Key: " + key + ", Value: " + extras.get(key));
                }
            }
            // 发送包含来电号码的本地广播
            Intent localIntent = new Intent(ACTION_INCOMING_CALL);
            localIntent.putExtra(EXTRA_INCOMING_NUMBER, incomingNumber);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        }
    }
}



