package com.application.services.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.application.services.receivers.IncomingSmsReceiver;
import com.application.services.utils.CommonLib;
import com.application.services.utils.CryptoHelper;
import com.application.services.utils.PostWrapper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class BookingService extends IntentService {

    public Context context;
    private SharedPreferences prefs;

    public BookingService() {
        super("BookingService");
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                String finalMsg = "";

                String senderNum = "";

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                    senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    finalMsg += message;
                }

                if(senderNum!=null && !"".equals(senderNum)) {
                    // here the magic happens ;)
                    CryptoHelper helper = new CryptoHelper();
                    String bookingString = null;
                    try {
                        bookingString = helper.decrypt(finalMsg, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(bookingString != null) {
                        String[] tokens = bookingString.split(",");
                        if(tokens != null && tokens.length == 5) {
                            String accessToken = tokens[0];
                            double latitude = Double.parseDouble(tokens[1]);
                            double longitude = Double.parseDouble(tokens[2]);
                            String deviceId = tokens[3];
                            Boolean cabType = Boolean.parseBoolean(tokens[4]);

                            Object result[] = null;
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("client_id", CommonLib.CLIENT_ID));
                            nameValuePairs.add(new BasicNameValuePair("app_type", CommonLib.APP_TYPE));
                            nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
                            nameValuePairs.add(new BasicNameValuePair("latitude", latitude+""));
                            nameValuePairs.add(new BasicNameValuePair("longitude", longitude+""));
                            nameValuePairs.add(new BasicNameValuePair("cabType", cabType+""));
                            nameValuePairs.add(new BasicNameValuePair("deviceId", deviceId));
                            nameValuePairs.add(new BasicNameValuePair("bookingType", "offline"));

                            try {
                                result = PostWrapper.postRequest(CommonLib.SERVER + "booking/book?", nameValuePairs,
                                        PostWrapper.CAB_BOOKING_REQUEST, context);
                            } catch (Exception e) {
                                e.printStackTrace();
                                result = null;
                            }

                            // check the output here
                        }
                    }
                }
            }

        } catch (Exception e) {
            //shit happened
            e.printStackTrace();
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        IncomingSmsReceiver.completeWakefulIntent(intent);
    }
}