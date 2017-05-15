package com.application.services.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Debug;

import com.application.services.ZApplication;

import java.util.ArrayList;
import java.util.Hashtable;

public class UploadManager {

    public static Hashtable<Integer, AsyncTask> asyncs = new Hashtable<Integer, AsyncTask>();
    public static Context context;
    private static SharedPreferences prefs;
    private static ArrayList<UploadManagerCallback> callbacks = new ArrayList<UploadManagerCallback>();
    private static ZApplication zapp;

    public static void setContext(Context context) {
        UploadManager.context = context;
        prefs = context.getSharedPreferences("application_settings", 0);

        if (context instanceof ZApplication) {
            zapp = (ZApplication) context;
        }
    }

    public static void addCallback(UploadManagerCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }

        // this is here because its called from a lot of places.
        if ((double) Debug.getNativeHeapAllocatedSize() / Runtime.getRuntime().maxMemory() > .70) {
            if (zapp != null) {

                if (zapp.cache != null)
                    zapp.cache.clear();
            }
        }
    }

    public static void removeCallback(UploadManagerCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

}