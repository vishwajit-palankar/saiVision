package com.saivision.collection;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Admin on 25/09/2016.
 */

public class SaiVisionApplication extends Application {

    private static SaiVisionApplication mInstance;
    private RequestQueue mRequestQueue;

    public static SaiVisionApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        mInstance = this;

    }
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
