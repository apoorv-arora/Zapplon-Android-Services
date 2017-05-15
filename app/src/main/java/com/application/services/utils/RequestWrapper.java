package com.application.services.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import com.application.services.ZApplication;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harsh on 6/6/2016.
 */

public class RequestWrapper {
	private static SharedPreferences prefs;
	private static ZApplication zapp;

	// cache time
	public static final int FAV = -1;
	public static final int TEMP = 86400;
	public static final int CONSTANT = 1209600;
	public static final int ONE_HOUR = 3600;
	public static final int THREE_HOURS = 3600 * 3;

	// contant identifiers
	public static final String USER_MESSAGES = "user_messages";
	public static final String MY_BOOKINGS = "my_bookings";

	public static void Initialize(Context context) {
		prefs = context.getSharedPreferences("application_settings", 0);
	}

	public static InputStream fetchhttp(String urlstring) {

		String value = null;
		try {

			CommonLib.ZLog("RW url", urlstring + ".");
			HttpPost httpPost = new HttpPost(urlstring);
			httpPost.addHeader(new BasicHeader("client_id", CommonLib.CLIENT_ID));
			httpPost.addHeader(new BasicHeader("app_type", CommonLib.APP_TYPE));
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("client_id", CommonLib.CLIENT_ID));
			nameValuePairs.add(new BasicNameValuePair("app_type", CommonLib.APP_TYPE));

			if (nameValuePairs != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			}

			long timeBeforeApiCall = System.currentTimeMillis();
			HttpResponse response = HttpManager.execute(httpPost);
			CommonLib.ZLog("fetchhttp(); Response Time: ", System.currentTimeMillis() - timeBeforeApiCall);

			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = CommonLib.getStream(response);
				return in;

			} else {
				CommonLib.ZLog("fetchhttp(); Response Code: ", responseCode + "-------" + urlstring);
			}
		} catch (Exception e) {
			CommonLib.ZLog("Error fetching http url", e.toString());
			e.printStackTrace();
		}
		return  null;
	}

	public static InputStream fetchhttpGet(String urlstring) {

		try {
			HttpGet get = new HttpGet(urlstring);

			HttpResponse response = HttpManager.execute(get);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = CommonLib.getStream(response);
				return in;

			} else {
				CommonLib.ZLog("fetchhttp(); Response Code: ", responseCode + "-------" + urlstring);
			}
		} catch (Exception e) {
			CommonLib.ZLog("Error fetching http url", e.toString());
			e.printStackTrace();
		}
		return  null;
	}

	public static Object RequestHttp(String url, String Object_Type, int status) {
		Object o = null;
		InputStream http_result;

		http_result = fetchhttp(url);
		o = parse(http_result, Object_Type);
		return o;
	}

	public static Object RequestHttpGet(String url, String Object_Type, int status) {
		Object o = null;
		InputStream http_result;

		http_result = fetchhttpGet(url);
		o = parse(http_result, Object_Type);
		return o;
	}

	public static Object parse(InputStream result, String Type) {

		Object o = null;

		if (Type == MY_BOOKINGS) {
			Object connectedAccounts = null;
			try {
				connectedAccounts = ParserJson.parse_CabBookingResponse(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return connectedAccounts;
		}

		return o;
	}

	public static byte[] Serialize_Object(Object O) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(O);
		out.close();

		// Get the bytes of the serialized object
		byte[] buf = bos.toByteArray();
		return buf;
	}

	public static Object Deserialize_Object(byte[] input, String Type) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(input));

		if (Type == USER_MESSAGES) {
			Message result = (Message) in.readObject();
			in.close();
			return result;
		} else if (Type.equals("")) {
			Object o = in.readObject();
			in.close();
			return o;
		} else {
			in.close();
			return null;
		}

	}

}
