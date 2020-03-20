package com.a.goldtrack;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.a.goldtrack.Interfaces.ConnectivityReceiverListener;
import com.a.goldtrack.broadcast.ConnectivityReceiver;

public class GTrackApplication extends Application {

    private static GTrackApplication mInstance;
    public static final String CHANNEL_ID = "dropDownServiceChannel";


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        createNotificationChannel();
    }

    public static synchronized GTrackApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
