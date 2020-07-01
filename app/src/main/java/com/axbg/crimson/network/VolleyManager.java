package com.axbg.crimson.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyManager {

    private static VolleyManager instance = null;
    private RequestQueue queue;

    private VolleyManager(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public static VolleyManager getInstance(Context context) {
        if (instance == null) {
            synchronized (VolleyManager.class) {
                if (instance == null) {
                    instance = new VolleyManager(context);
                }
            }
        }

        return instance;
    }

    public void addToQueue(StringRequest request) {
        if (instance != null) {
            instance.queue.add(request);
        }
    }
}
