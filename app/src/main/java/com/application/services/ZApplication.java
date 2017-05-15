package com.application.services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

import com.application.services.services.CacheCleanerService;
import com.application.services.utils.CommonLib;
import com.application.services.utils.LruCache;
import com.application.services.utils.PostWrapper;
import com.application.services.utils.RequestWrapper;
import com.application.services.utils.UploadManager;
import com.application.services.utils.location.ZLocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Calendar;
import java.util.List;

public class ZApplication extends Application {

    public LruCache<String, Bitmap> cache;
    public ZLocationListener zll = new ZLocationListener(this);
    public LocationManager locationManager = null;
    public String location = "";
    public String country = "";
    public double lat = 0;
    public double lon = 0;
    public boolean isNetworkProviderEnabled = false;
    public boolean isGpsProviderEnabled = false;
    public boolean firstLaunch = false;
    public int state = CommonLib.LOCATION_DETECTION_RUNNING;

    private CheckLocationTimeoutAsync checkLocationTimeoutThread;

    public void onCreate() {
        super.onCreate();
        cache = new LruCache<String, Bitmap>(30);
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        try {
            lat = Double.parseDouble(prefs.getString("lat1", "0"));
            lon = Double.parseDouble(prefs.getString("lon1", "0"));
        } catch (ClassCastException e) {
        } catch (Exception e) {
        }
        location = prefs.getString("location", "");

        // Managers initialize
        RequestWrapper.Initialize(getApplicationContext());
        UploadManager.setContext(getApplicationContext());
        PostWrapper.Initialize(getApplicationContext());
        if (prefs.getInt("version", 0) < CommonLib.VERSION) {

            // the logic in this block is used on Home.java, to determine
            // whether to show collection first run or not.
            if (prefs.getInt("version", 0) == 0) {
                prefs.edit().putBoolean("app_fresh_install", true).commit();
                prefs.edit().putBoolean("app_upgrade", false).commit();

            } else if (prefs.getInt("version", 0) > 0) {
                prefs.edit().putBoolean("app_upgrade", true).commit();
                prefs.edit().putBoolean("app_fresh_install", false).commit();
            }

            firstLaunch = true;
            Editor edit = prefs.edit();

            edit.putBoolean("firstLaunch", true);
            edit.putInt("version", CommonLib.VERSION);
            edit.commit();

            deleteDatabase("CACHE");
            deleteDatabase("STORESDB");

            startCacheCleanerService();

        } else {
            firstLaunch = prefs.getBoolean("firstLaunch", false);
        }

        try {
            if (!isMyServiceRunning(CacheCleanerService.class)) {
                boolean alarmUp = (PendingIntent.getService(this, 0, new Intent(this, CacheCleanerService.class), PendingIntent.FLAG_NO_CREATE) != null);

                if (!alarmUp)
                    startCacheCleanerService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new ThirdPartyInitAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }


    private class ThirdPartyInitAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                // Crashlytics Initialize
                //Fabric.with(getApplicationContext(), new Crashlytics());
//                AppsFlyerLib.getInstance().startTracking(this, "JaSzZ3NAMHzDBv3LmfTk69");
//                String appFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext());
//                AppsFlyerLib.getInstance().registerConversionListener(getApplicationContext(), new AppsFlyerConversionListener() {
//                    @Override
//                    public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
//                        for (String attrName : conversionData.keySet()) {
//                            CommonLib.ZLog(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " +
//                                    conversionData.get(attrName));
//                        }
//                        //SCREEN VALUES//
////                        final String install_type = "Install Type: " + conversionData.get("af_status");
////                        final String media_source = "Media Source: " + conversionData.get("media_source");
////                        final String install_time = "Install Time(GMT): " + conversionData.get("install_time");
////                        final String click_time = "Click Time(GMT): " + conversionData.get("click_time");
//                    }
//
//                    @Override
//                    public void onInstallConversionFailure(String errorMessage) {
//                        CommonLib.ZLog(AppsFlyerLib.LOG_TAG, "error getting conversion data: " + errorMessage);
////                        ((TextView) findViewById(R.id.logView)).setText(errorMessage);
//                    }
//
//                    @Override
//                    public void onAppOpenAttribution(Map<String, String> conversionData) {
//                    }
//
//                    @Override
//                    public void onAttributionFailure(String errorMessage) {
//                        CommonLib.ZLog(AppsFlyerLib.LOG_TAG, "error onAttributionFailure : " + errorMessage);
//                    }
//                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startCacheCleanerService() {

        Intent intent = new Intent(this, CacheCleanerService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 04);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pintent);
    }

    public void setLocationString(String lstr) {
        location = lstr;
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        Editor editor = prefs.edit();
        editor.putString("location", location);
        editor.commit();
    }

    public void setCountryString(String lstr) {
        country = lstr;
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        Editor editor = prefs.edit();
        editor.putString("country", country);
        editor.commit();
    }

    public void setAddressString(String lstr) {
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        Editor editor = prefs.edit();
        editor.putString("address", lstr);
        editor.commit();
    }

    public String getAddressString() {
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        String address = prefs.getString("address", "");
        return address;
    }

    public String getLocationString() {
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        location = prefs.getString("location", "");
        return location;
    }

    public String getCountryString() {
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        location = prefs.getString("country", "");
        return country;
    }

    public void interruptLocationTimeout() {
        // checkLocationTimeoutThread.interrupt();
        if (checkLocationTimeoutThread != null)
            checkLocationTimeoutThread.interrupt = false;
    }

    public void startLocationCheck() {

        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (result == ConnectionResult.SUCCESS) {
            zll.getFusedLocation(this);
        } else {
            getAndroidLocation();
        }
    }

    public void getAndroidLocation() {

        CommonLib.ZLog("zll", "getAndroidLocation");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        if (providers != null) {
            for (String providerName : providers) {
                if (providerName.equals(LocationManager.GPS_PROVIDER))
                    isGpsProviderEnabled = true;
                if (providerName.equals(LocationManager.NETWORK_PROVIDER))
                    isNetworkProviderEnabled = true;
            }
        }

        if (isNetworkProviderEnabled || isGpsProviderEnabled) {

            if (isGpsProviderEnabled)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, zll);
            if (isNetworkProviderEnabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, zll);

            if (checkLocationTimeoutThread != null) {
                checkLocationTimeoutThread.interrupt = false;
            }

            checkLocationTimeoutThread = new CheckLocationTimeoutAsync();
            checkLocationTimeoutThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            zll.locationNotEnabled();
        }
    }

    private class CheckLocationTimeoutAsync extends AsyncTask<Void, Void, Void> {
        boolean interrupt = true;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            if (interrupt) {
                zll.interruptProcess();
            }
        }
    }

    public boolean isLocationAvailable() {
        return (isNetworkProviderEnabled || isGpsProviderEnabled);
    }

    @Override
    public void onLowMemory() {
        cache.clear();
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        int userId = prefs.getInt("uid", 0);
        super.onLowMemory();
    }

    public void onTrimLevel(int i) {
        cache.clear();
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        int userId = prefs.getInt("uid", 0);
        super.onTrimMemory(i);
    }

    // GA

    public void logout()
    {
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        Editor editor = prefs.edit();
        editor.putInt("uid", 0);
        editor.putString("thumbUrl", "");
        editor.putString("access_token", "");
        editor.remove("username");
        editor.remove("description");
        editor.remove("verified");
        editor.remove("phone");
        editor.remove("profile_pic");
        editor.remove("HSLogin");
        editor.remove("INSTITUTION_NAME");
        editor.remove("STUDENT_ID");
        editor.putBoolean("facebook_post_permission", false);
        editor.putBoolean("post_to_facebook_flag", false);
        editor.putBoolean("facebook_connect_flag", false);
        editor.putBoolean("twitter_status", false);
        editor.remove("ola_access_token");
        editor.remove("uber_access_token");
        editor.remove("ola_cab_session_id");
        editor.remove("uber_cab_session_id");
        editor.remove("appConfig_title");
        editor.remove("appConfig_description");
        editor.remove("appConfig_imageUrl");
        editor.remove("appConfig_footer");
        editor.remove("appConfig_dialog");
        editor.remove("appConfig_hasChanged");
        editor.remove("appConfig_hasChanged_new");
        editor.remove("appConfig_finishonTouchOutside");
        editor.remove("appConfig_showAlways");
        editor.commit();
    }

}
