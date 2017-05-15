package com.application.services.utils;

import com.application.services.data.Booking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ParserJson {

	@SuppressWarnings("resource")
	public static JSONObject convertInputStreamToJSON(InputStream is) throws JSONException {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		String responseJSON = s.hasNext() ? s.next() : "";

		CommonLib.ZLog("response", responseJSON);
		JSONObject map = new JSONObject(responseJSON);
		CommonLib.ZLog("RESPONSE", map.toString(2));
		return map;
	}

	public static Object[] parseGenericResponse(InputStream is) throws JSONException {

		Object[] output = new Object[]{"failed", "", null};

		JSONObject responseObject = ParserJson.convertInputStreamToJSON(is);

		if (responseObject != null && responseObject.has("status")) {
			output[0] = responseObject.getString("status");
			if (output[0].equals("success")) {
				if (responseObject.has("response"))
					output[1] = responseObject.getString("response");
			} else {
				if (responseObject.has("errorMessage")) {
					output[1] = responseObject.getString("errorMessage");
				}
			}
		}
		return output;
	}

	public static ArrayList<Booking> parse_CabBookingResponse(InputStream is) throws JSONException {

		JSONObject responseObject = ParserJson.convertInputStreamToJSON(is);
		ArrayList<Booking> connectedAccounts = new ArrayList<Booking>();
		if (responseObject != null && responseObject.has("response") && responseObject.get("response") instanceof JSONArray) {
			JSONArray uberJsonArray = responseObject.getJSONArray("response");
			for (int i = 0; i < uberJsonArray.length(); i++) {
				if (uberJsonArray.get(i) instanceof JSONObject) {
					JSONObject connectedAccountJson = uberJsonArray.getJSONObject(i);
					Booking connectedAccount = parse_CabBookingObject(connectedAccountJson);
					connectedAccounts.add(connectedAccount);
				}
			}
		}
		return connectedAccounts;
	}

	public static Object[] parse_BookingResponse(InputStream is) throws JSONException {

		Object[] output = new Object[]{"failed", "", null};

		JSONObject responseObject = ParserJson.convertInputStreamToJSON(is);

		return output;
	}

	public static Booking parse_CabBookingObject(JSONObject cabJson) {
		if(cabJson == null)
			return null;

		Booking cabBookingDetails = new Booking();
		try {

			if (cabJson.has("email")) {
				cabBookingDetails.setEmail(String.valueOf(cabJson.get("email")));
			}
			if (cabJson.has("status")) {
				cabBookingDetails.setStatus(String.valueOf(cabJson.get("status")));
			}
			if (cabJson.has("time")) {
				cabBookingDetails.setTime(String.valueOf(cabJson.get("time")));
			}
			if (cabJson.has("bookingId")) {
				cabBookingDetails.setBookingId(String.valueOf(cabJson.get("bookingId")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cabBookingDetails;
	}
}