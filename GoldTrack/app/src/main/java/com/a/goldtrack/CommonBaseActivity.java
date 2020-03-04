package com.a.goldtrack;

import androidx.appcompat.app.AppCompatActivity;

import com.a.goldtrack.Interfaces.ConnectivityReceiverListener;

public class CommonBaseActivity extends AppCompatActivity implements ConnectivityReceiverListener {


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


}
