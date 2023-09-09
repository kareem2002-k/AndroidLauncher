package com.example.launcherwithadmin;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        // Called when the user has granted device admin privileges to your app.
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // Called when a user tries to disable device admin privileges for your app.
        // You can provide a warning or explanation message here.
        return "Disabling device admin will remove some security features. Are you sure you want to continue?";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        // Called when the user has disabled device admin privileges for your app.
    }
}
