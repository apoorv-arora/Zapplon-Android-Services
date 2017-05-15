package com.application.services.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class CommonLib {

    public final static boolean ZapplonLog = true;
    private static SharedPreferences prefs;

	public static String SERVER_WITHOUT_VERSION = "http://api.zapplon.com:8080/v1/rest/";

    public static String API_VERSION = "";
    public static String SERVER = SERVER_WITHOUT_VERSION + API_VERSION;

    /**
     * Preferences
     */
    public final static String APP_SETTINGS = "application_settings";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";

    public static String Thin = "fonts/zapplon_Thin.otf";
    public static String Light = "fonts/zapplon_Light.otf";
    public static String Bold = "fonts/zapplon_Bold.otf";
    public static String Icons = "fonts/zapplon_Icon.ttf";
    public static String BOLD_FONT_FILENAME = "fonts/zapplon_Bold.otf";

    /**
     * Application version
     */
    public static final int VERSION = 32;
    public static final String VERSION_STRING = "2.58";

    /**
     * Authorization params
     */
    public static final String SOURCE = "&source=android_market&version=" + android.os.Build.VERSION.RELEASE
            + "&app_version=" + VERSION;
    public static final String CLIENT_ID = "bt_android_client";
    public static final String APP_TYPE = "bt_android";

    /**
     * Thread pool executors
     */
    private static final int mImageAsyncsMaxSize = 4;
    public static final BlockingQueue<Runnable> sPoolWorkQueueImage = new LinkedBlockingQueue<Runnable>(128);
    private static ThreadFactory sThreadFactoryImage = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    };
    public static final Executor THREAD_POOL_EXECUTOR_IMAGE = new ThreadPoolExecutor(mImageAsyncsMaxSize,
            mImageAsyncsMaxSize, 1, TimeUnit.SECONDS, sPoolWorkQueueImage, sThreadFactoryImage);

    /**
     * Upload status tracker
     */
    public static final int REQUEST_PRE_SIGNUP = 201;
    public static final int GOOGLE_LOGIN = 202;
    public static final int LOGOUT = 203;
    public static final int WISH_ADD = 204;
    public static final int WISH_REMOVE = 205;
    public static final int HARDWARE_REGISTER = 206;
    public static final int UPDATE_INSTITUTION = 207;
    public static final int WISH_UPDATE_STATUS = 208;
    public static final int SEND_MESSAGE = 209;
    public static final int LOCATION_UPDATE = 210;
    public static final int REDEEM_COUPON = 211;
    public static final int BILLING_UPDATE = 212;
    public static final int SEND_FEEDBACK = 213;
    public static final int TABLE_BOOKING = 214;
    public static final int CAB_BOOKING = 215;
    public static final int PHONE_VERIFICATION = 216;
    public static final int SEND_CAB_TOKEN = 217;
    public static final int GET_CONNECTED_ACCOUNTS = 218;
    public static final int DISCONNECT_CONNECTED_ACCOUNTS = 219;
    public static final int CAB_BOOKING_REQUEST = 220;
    public static final int CAB_CANCELLATION_REQUEST = 221;
    public static final int INVITATION_ID = 222;
    public static final int GET_VOUCHER = 223;
    public static final int AVAIL_VOUCHER = 224;
    public static final int ADD_ADDRESS = 225;
    public static final int DELETE_ADDRESS = 226;
    public static final int VALIDATE_COUPON = 227;
    public static final int LOGIN = 228;
    public static final int SIGNUP = 229;

    public static final int CAB_SEDAN = 13;
    public static final int CAB_COMPACT = 14;
    public static final int CAB_LUXURY = 15;
    public static final int BIKE = 16;
    public static final int AUTO = 17;

    public static final int TYPE_UBER = 5;
    public static final int TYPE_OLA = 6;
    public static final int TYPE_EASY = 7;
    public static final int TYPE_JUGNOO = 8;
    public static final int TYPE_MEGA = 9;


    /**
     * Constant to track location identification progress
     */
    public static final int LOCATION_NOT_ENABLED = 0;
    /**
     * Constant to track location identification progress
     */
    public static final int LOCATION_NOT_DETECTED = 1;
    /**
     * Constant to track location identification progress
     */
    public static final int LOCATION_DETECTED = 2;
    /**
     * Constant to track location identification progress
     */
    public static final int GETZONE_CALLED = 3;
    /**
     * Constant to track location identification progress
     */
    public static final int CITY_IDENTIFIED = 4;
    /**
     * Constant to track location identification progress
     */
    public static final int CITY_NOT_IDENTIFIED = 5;
    public static final int LOCATION_DETECTION_RUNNING = 6;
    public static final int DIFFERENT_CITY_IDENTIFIED = 7;

    public static final String NOTIFICATION_TYPE_PROMOTIONAL = "NOTIFICATION_TYPE_PROMOTIONAL";

    public static final String NOTIFICATION_TYPE_CASHBACK_POINTS_ADDED = "NOTIFICATION_TYPE_CASHBACK_POINTS_ADDED";
    public static final String NOTIFICATION_TYPE_REFERRAL_POINTS_ADDED = "NOTIFICATION_TYPE_REFERRAL_POINTS_ADDED";

    public static final String NOTIFICATION_TYPE_BOOKING_CONFIRMED = "NOTIFICATION_TYPE_BOOKING_CONFIRMED";
    public static final String NOTIFICATION_TYPE_BOOKING_CANCELLED = "NOTIFICATION_TYPE_BOOKING_CANCELLED";

    public static final int ADDRESS_TYPE_HOME = 201;
    public static final int ADDRESS_TYPE_WORK = 202;


    // Return this string for every call
    public static String getVersionString(Context context) {
        String uuidString = "";

        if (prefs == null && context != null)
            prefs = context.getSharedPreferences(APP_SETTINGS, 0);

        if (prefs != null)
            uuidString = "&uuid=" + prefs.getString("app_id", "");

        return SOURCE + uuidString;
    }

    // Calculate the sample size of bitmaps
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int inSampleSize = 1;
        double ratioH = (double) options.outHeight / reqHeight;
        double ratioW = (double) options.outWidth / reqWidth;

        int h = (int) Math.round(ratioH);
        int w = (int) Math.round(ratioW);

        if (h > 1 || w > 1) {
            if (h > w) {
                inSampleSize = h >= 2 ? h : 2;

            } else {
                inSampleSize = w >= 2 ? w : 2;
            }
        }
        return inSampleSize;
    }

    public static final Hashtable<String, Typeface> typefaces = new Hashtable<String, Typeface>();

    public static Typeface getTypeface(Context c, String name) {
        synchronized (typefaces) {
            if (!typefaces.containsKey(name)) {
                try {
                    InputStream inputStream = c.getAssets().open(name);
                    File file = createFileFromInputStream(inputStream, name);
                    if (file == null) {
                        return Typeface.DEFAULT;
                    }
                    Typeface t = Typeface.createFromFile(file);
                    typefaces.put(name, t);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Typeface.DEFAULT;
                }
            }
            return typefaces.get(name);
        }
    }

    private static File createFileFromInputStream(InputStream inputStream, String name) {

        try {
            File f = File.createTempFile("font", null);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return f;
        } catch (Exception e) {
            // Logging exception
            e.printStackTrace();
        }

        return null;
    }

    public static int getStatusBarHeight(Context mContext) {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // Zapplon Logging end points
    public static void ZLog(String Tag, String Message) {
        if (ZapplonLog && Message != null)
            Log.i(Tag, Message);
    }

    public static void ZLog(String Tag, float Message) {
        if (ZapplonLog)
            Log.i(Tag, Message + "");
    }

    public static void ZLog(String Tag, boolean Message) {
        if (ZapplonLog)
            Log.i(Tag, Message + "");
    }

    public static void ZLog(String Tag, int Message) {
        if (ZapplonLog)
            Log.i(Tag, Message + "");
    }

    public static InputStream getStream(HttpResponse response) throws IllegalStateException, IOException {
        InputStream instream = response.getEntity().getContent();
        Header contentEncoding = response.getFirstHeader("Content-Encoding");
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            instream = new GZIPInputStream(instream);
        }
        return instream;
    }

    // Checks if network is available
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return distance in km
     */

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }

    // Returns the Network State
    public static String getNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String returnValue = "";
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                returnValue = "wifi";
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                returnValue = "mobile" + "_" + getNetworkType(context);
            else
                returnValue = "Unknown";
        } else
            returnValue = "Not connected";
        return returnValue;
    }

    // Returns the Data Network type
    public static String getNetworkType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {

            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";

            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";

            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE ";

            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "EHRPD ";

            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO_0 ";

            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO_A ";

            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO_B ";

            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS ";

            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA ";

            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA ";

            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPAP ";

            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA ";

            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "IDEN ";

            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE ";

            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS ";

            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "UNKNOWN ";

            default:
                return "UNKNOWN ";
        }
    }

    // check done before storing the bitmap in the memory
    public static boolean shouldScaleDownBitmap(Context context, Bitmap bitmap) {
        if (context != null && bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            return ((width != 0 && width / bitmap.getWidth() < 1) || (height != 0 && height / bitmap.getHeight() < 1));
        }
        return false;
    }

    public static boolean isAndroidL() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    public static String getDateFromUTC(long timestamp) {
        Date date = new Date(timestamp);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return (cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DATE) + " " + cal.get(Calendar.HOUR) + ":"
                + cal.get(Calendar.MINUTE) + (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
    }

    public static String getUberTimeString(long seconds) {
        StringBuilder builder = new StringBuilder();

        if (seconds < 60) {
            builder.append(seconds);
            if (seconds == 1)
                builder.append(" sec");
            else
                builder.append(" secs");
            return builder.toString();
        } else if (seconds < (60 * 60)) {
            builder.append((seconds / 60));
            if ((seconds / 60) == 1)
                builder.append(" min");
            else
                builder.append(" mins");
            return builder.toString();
        } else if (seconds < (60 * 60 * 60)) {
            builder.append((seconds / (60 * 60)));
            if ((seconds / (60 * 60)) == 1)
                builder.append(" hour");
            else
                builder.append(" hours");
            return builder.toString();
        } else
            return "";
    }

    public static String getUberTimeStringShort(long estimate, String timeUnit) {
        StringBuilder builder = new StringBuilder();

        if(timeUnit != null) {
            if(timeUnit.startsWith("s")) {
                if (estimate < 60) {
                    builder.append(estimate);
                    builder.append("s");
                    return builder.toString();
                } else if (estimate < (60 * 60)) {
                    builder.append((estimate / 60));
                    builder.append("m");
                    return builder.toString();
                } else if (estimate < (60 * 60 * 60)) {
                    builder.append((estimate / (60 * 60)));
                    builder.append("h");
                    return builder.toString();
                } else
                    return "";
            }
            else if(timeUnit.startsWith("m")) {
                if (estimate < 60) {
                    builder.append(estimate);
                    builder.append("m");
                    return builder.toString();
                } else if (estimate < (60 * 60)) {
                    builder.append((estimate / 60));
                    builder.append("h");
                    return builder.toString();
                } else
                    return "";
            }
            else if(timeUnit.startsWith("h")) {
                builder.append(estimate);
                builder.append("h");
                return builder.toString();
            } else
                return "";
        } else if (estimate < 60) {
            builder.append(estimate);
            builder.append("s");
            return builder.toString();
        } else if (estimate < (60 * 60)) {
            builder.append((estimate / 60));
            builder.append("m");
            return builder.toString();
        } else if (estimate < (60 * 60 * 60)) {
            builder.append((estimate / (60 * 60)));
            builder.append("h");
            return builder.toString();
        } else
            return "";
    }

    public static String getPriceString(String currencyCode, String value, boolean isLeft) {

        if (currencyCode == null)
            return "Rs. " + value;

        if (currencyCode != null && (currencyCode.equals("INR") || currencyCode.equals("")))
            return "Rs. " + value;

        if (isLeft) {
            return currencyCode + " " + value;
        } else {
            return value + " " + currencyCode;
        }
    }


    /**
     * Returns the bitmap associated
     */
    public static Bitmap getBitmap(Context mContext, int resId, int width, int height) throws OutOfMemoryError {
        if (mContext == null)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(mContext.getResources(), resId, options);
        options.inSampleSize = CommonLib.calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565;

        if (!CommonLib.isAndroidL())
            options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId, options);

        return bitmap;
    }

    /**
     * Blur a bitmap with the radius associated
     */
    public static Bitmap fastBlur(Bitmap bitmap, int radius) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int[] pix = new int[w * h];
            CommonLib.ZLog("pix", w + " " + h + " " + pix.length);
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);

            Bitmap blurBitmap = bitmap.copy(bitmap.getConfig(), true);

            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;

            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];

            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }

            yw = yi = 0;

            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;

            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;

                for (x = 0; x < w; x++) {

                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];

                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    sir = stack[i + radius];

                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];

                    rbs = r1 - Math.abs(i);

                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];

                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi += w;
                }
            }

            CommonLib.ZLog("pix", w + " " + h + " " + pix.length);
            blurBitmap.setPixels(pix, 0, w, 0, 0, w, h);
            return blurBitmap;

        } catch (OutOfMemoryError e) {
            return bitmap;
        } catch (Exception e) {
            return bitmap;
        }
    }

    public static Bitmap getBitmapFromDisk(String url, Context ctx) {

        Bitmap defautBitmap = null;
        try {
            String filename = constructFileName(url);
            File filePath = new File(ctx.getCacheDir(), filename);

            if (filePath.exists() && filePath.isFile() && !filePath.isDirectory()) {
                FileInputStream fi;
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Config.RGB_565;
                fi = new FileInputStream(filePath);
                defautBitmap = BitmapFactory.decodeStream(fi, null, opts);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (Exception e) {

        } catch (OutOfMemoryError e) {

        }

        return defautBitmap;
    }

    public static String constructFileName(String url) {
        return url.replaceAll("/", "_");
    }


    public static void addBitmapToDisk(String url, Bitmap bmp, Context ctx) {
        writeBitmapToDisk(url, bmp, ctx, CompressFormat.PNG);
    }

    public static void writeBitmapToDisk(String url, Bitmap bmp, Context ctx, CompressFormat format) {
        FileOutputStream fos;
        String fileName = constructFileName(url);
        try {
            if (bmp != null) {
                fos = new FileOutputStream(new File(ctx.getCacheDir(), fileName));
                bmp.compress(format, 75, fos);
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //GA
    public enum TrackerName {
        GLOBAL_TRACKER,
        APPLICATION_TRACKER
    }

    public static Bitmap getRoundedCornerBitmap(final Bitmap bitmap, final float roundPx) {

        if (bitmap != null) {
            try {
                final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                final RectF rectF = new RectF(rect);

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                return output;

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * Remove the keyboard explicitly.
     */
    public static void hideKeyBoard(Activity mActivity, View mGetView) {
        try {
            ((InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mGetView.getRootView().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //IMEISV
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imeisv = telephonyManager.getDeviceId();
        if (imeisv == null)
            imeisv = "Unknown";
        return imeisv;
    }


    public static void showSoftKeyboard(Context context, View v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static String getCabCompanyName(int type) {
        String retValue;
        switch (type) {
            case CommonLib.TYPE_OLA:
                retValue = "OLA";
                break;
            case CommonLib.TYPE_UBER:
                retValue = "uber";
                break;
            default:
                retValue = "";
        }
        return retValue;
    }

    public static boolean isDayTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return true;
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            return true;
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            return true;
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            return false;
        }
        return true;
    }

}
