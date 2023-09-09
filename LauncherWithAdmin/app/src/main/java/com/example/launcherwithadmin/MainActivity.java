package com.example.launcherwithadmin;

import androidx.appcompat.app.AppCompatActivity;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager deviceManger;
    private ComponentName adminComponent;
    private EditText pinEditText;
    private Button exitButton;

    private static final int REQUEST_DEVICE_ADMIN_PERMISSION = 1;
    private static final int REQUEST_ENABLE_DEVICE_ADMIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DevicePolicyManager and admin component
        deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);

        if (deviceManger.isDeviceOwnerApp(getPackageName())) {
            setDefaultCosuPolicies(true);
        } else {
            Toast.makeText(this, "Kiosk mode is not active.", Toast.LENGTH_SHORT).show();
        }

        // Initialize UI components
        pinEditText = findViewById(R.id.pinEditText);
        exitButton = findViewById(R.id.exitButton);

        // Set click listener for the exit button
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitKioskMode();
            }
        });
    }

    // Method to exit kiosk mode
    private void exitKioskMode() {
        String enteredPIN = pinEditText.getText().toString();
        String correctPIN = "1234"; // Replace with your actual PIN

        if (enteredPIN.equals(correctPIN)) {
            // Disable kiosk mode and exit
            if (deviceManger.isLockTaskPermitted(this.getPackageName())) {
                stopLockTask(); // Exit Kiosk Mode
            } else {
                Toast.makeText(this, "Kiosk mode is not active.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Display an error message for incorrect PIN
            Toast.makeText(this, "Incorrect PIN. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDefaultCosuPolicies(boolean active) {
        if (active) {
            deviceManger.setSystemUpdatePolicy(adminComponent, SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            deviceManger.setSystemUpdatePolicy(adminComponent, null);
        }
        deviceManger.setLockTaskPackages(adminComponent, active ? new String[]{getPackageName()} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // on reboot
            deviceManger.addPersistentPreferredActivity(adminComponent, intentFilter, new ComponentName(getPackageName(), MyDeviceAdminReceiver.class.getName()));
        } else {
            deviceManger.clearPackagePersistentPreferredActivities(adminComponent, getPackageName());
        }
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            deviceManger.addUserRestriction(adminComponent, restriction);
        } else {
            deviceManger.clearUserRestriction(adminComponent, restriction);
        }
    }
}
