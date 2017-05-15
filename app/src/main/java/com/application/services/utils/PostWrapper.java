package com.application.services.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.application.services.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class PostWrapper {

	private static SharedPreferences prefs;

	/** Constants */
	public static String PRESIGNUP = "pre_signup";
	public static String LOGOUT = "logout";
	public static String LOGIN = "register";
	public static String SIGNUP = "signup";
	public static String WISH_POST = "wish_post";
	public static String WISH_DELETE = "wish_delete";
	public static String HARDWARE_REGISTER = "hardware_register";
	public static String INSTITUTION_ID = "update_institution_id";
	public static String WISH_STATUS_UPDATE = "wish_status_update";
	public static String SEND_MESSAGE = "send_message";
	public static String LOCATION_UPDATE = "update_location";
	public static String REDEEM_COUPON = "redeem_coupon";
	public static String BILLING_UPDATE = "billing_update";
	public static String SEND_FEEDBACK = "send_feedback";
	public static String GOOGLE_LOGIN = "google_login";
	public static String TABLE_BOOKING = "table_booking";
	public static String CAB_BOOKING = "cab_booking";
	public static String PHONE_VERFICATION = "phone_verification";
	public static String OLA_ACCESS_TOKEN= "ola_access_token";
	public static String GET_CONNECTED_ACCOUNT = "get_connected_account";
	public static String DISCONNECT_CONNECTED_ACCOUNT = "disconnect_connected_account";
	public static String CAB_BOOKING_REQUEST = "cab_booking_request";
	public static String CAB_CANCELLATION_REQUEST = "cab_cancellation_request";
	public static String INVITATION_ID = "invitation_id";
	public static String REFERRER = "referrer";
	public static String GET_VOUCHER = "get_voucher";
	public static String AVAIL_VOUCHER = "voucher_id";
	public static String ADD_ADDRESS = "add_address";
	public static String DELETE_ADDRESS = "delete_address";
	public static String VALIDATE_COUPON = "validate_coupon";
	public static String CHECK_BOOKING_STATUS = "check_booking_status";

	public static void Initialize(Context context) {
		// helper = new ResponseCacheManager(context);
		prefs = context.getSharedPreferences("application_settings", 0);
	}

	public static String convertStreamToString(InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	public static Object[] postRequest(String Url, List<NameValuePair> nameValuePairs, String type,
									   Context appContext) {

		Object[] resp = new Object[] { "failed", appContext.getResources().getString(R.string.could_not_connect),
				null };

		try {

			HttpResponse response = getPostResponse(Url, nameValuePairs, appContext);
			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream is = CommonLib.getStream(response);

				if (type.equals(CAB_BOOKING_REQUEST)) {
					resp = ParserJson.parse_BookingResponse(is);
				} else if (type.equals(CHECK_BOOKING_STATUS)){
					resp = ParserJson.parse_BookingResponse(is);
				}

			}
			// else {
			// logErrorResponse(url, response);
			// }

		} catch (Exception E) {
			E.printStackTrace();
			return resp;
		}
		return resp;
	}

	public static HttpResponse getPostResponse(String Url, List<NameValuePair> nameValuePairs, Context appContext)
			throws Exception {

		HttpPost httpPost = new HttpPost(Url + CommonLib.getVersionString(appContext));
		httpPost.addHeader(new BasicHeader("client_id", CommonLib.CLIENT_ID));
		httpPost.addHeader(new BasicHeader("app_type", CommonLib.APP_TYPE));

		if (nameValuePairs != null)
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

		return HttpManager.execute(httpPost);
	}

}
