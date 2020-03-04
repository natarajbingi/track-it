package com.a.goldtrack;

import android.app.Application;

import com.a.goldtrack.Interfaces.ConnectivityReceiverListener;
import com.a.goldtrack.broadcast.ConnectivityReceiver;

public class GTrackApplication extends Application {

    private static GTrackApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized GTrackApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
