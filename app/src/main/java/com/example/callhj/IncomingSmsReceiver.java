package com.example.callhj;import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;public class IncomingSmsReceiver extends BroadcastReceiver {
    private Context mContext; // 上下文对象

    public IncomingSmsReceiver() {
        this.mContext = null;
    }
    public IncomingSmsReceiver(Context context) {
        mContext = context; // 初始化上下文对象
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    StringBuilder messageBody = new StringBuilder();
                    String sender = "";

                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        messageBody.append(messages[i].getMessageBody());
                        if (sender.isEmpty()) {
                            sender = messages[i].getOriginatingAddress();
                        }
                    }

                    if (mContext != null) {
                        handleIncomingSms(sender, messageBody.toString());
                    }
                }
            }
        }
    }
    // 处理接收到的短信
    private void handleIncomingSms(String sender, String messageBody) {
        // 在此处处理收到的短信，例如发送广播或更新 UI
        // 示例：发送广播给您的 Activity，以便更新 UI
        Intent smsReceivedIntent = new Intent("com.example.callhj.INCOMING_SMS");
        smsReceivedIntent.putExtra("sender", sender);
        smsReceivedIntent.putExtra("message_body", messageBody);
        mContext.sendBroadcast(smsReceivedIntent);
    }
}
