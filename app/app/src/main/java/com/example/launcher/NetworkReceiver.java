package com.example.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

public class NetworkReceiver extends BroadcastReceiver {
    private TextView networkTextView;

    public NetworkReceiver(TextView networkTextView) {
        this.networkTextView = networkTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check network status
        String status = "Network: ";
        boolean isConnected = false;

        // Check Wi-Fi status
        if (isConnectedToWifi(context)) {
            status += "Wi-Fi";
            isConnected = true;
        }

        // Check mobile data status
        if (isConnectedToMobileData(context)) {
            if (isConnected) {
                status += ", ";
            }
            status += "Mobile Data";
        }

        // Update the network TextView
        networkTextView.setText(status);
    }

    private boolean isConnectedToWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    private boolean isConnectedToMobileData(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
}
