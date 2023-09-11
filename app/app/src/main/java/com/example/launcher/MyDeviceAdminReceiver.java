package com.example.launcher;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // Your implementation here
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        // Your implementation here
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // Your implementation here
        return "Your confirmation message";
    }
}
