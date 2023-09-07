package com.example.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.util.Log; // Import Log


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.view.WindowManager;

import android.view.View;
import android.widget.TextView;

import android.os.Handler;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;





import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// ...

public class MainActivity extends AppCompatActivity {

    private List<AppInfo> appInfoList;
    private RecyclerView recyclerView;
    private AppAdapter appAdapter;

    private TextView clockTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Enable immersive mode (hide status bar and navigation bar)
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Disable swipe-down gesture to show status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);



        // Check if screen pinning is supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                if (!activityManager.isInLockTaskMode()) {
                    startLockTask(); // Start screen pinning
                }
            }
        }

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 4 columns, adjust as needed

        appInfoList = new ArrayList<>();
        appAdapter = new AppAdapter(appInfoList, new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppInfo appInfo) {
                launchApp(appInfo);
            }
        });

        recyclerView.setAdapter(appAdapter);

        // Get a list of all installed activities (apps)
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infoList = packageManager.queryIntentActivities(intent, 0);

        // Define a set of package names for allowed system apps
        Set<String> allowedSystemApps = new HashSet<>(Arrays.asList(
                "com.android.camera",    // Camera app
                "com.android.settings",
                "com.android.camera2",
                "com.google.android.apps.maps",
                "com.google.android.apps.photos",
                "com.example.launcher"// Settings app

                // Add other allowed system apps here
        ));

        for (ResolveInfo info : infoList) {
            // Get the package name of the app
            String packageName = info.activityInfo.packageName;

            // Get the package name of the app
            String k = info.activityInfo.packageName;
            Log.d("AppDebug", "Package Name: " + k); // Log package name for debugging

            // Check if the app is allowed (system app) a
            if (allowedSystemApps.contains(packageName)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setLabel(info.loadLabel(packageManager).toString());
                appInfo.setPackageName(packageName); // Use the package name directly
                appInfo.setIcon(info.loadIcon(packageManager));

                appInfoList.add(appInfo);
            }
        }

        appAdapter.notifyDataSetChanged();





        // Initialize the clock TextView
        clockTextView = findViewById(R.id.clockTextView);

        // Create a handler to update the clock
        final Handler handler = new Handler();

        // Create a runnable to update the clock
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Get the current time
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("ar"));
                String currentTime = sdf.format(calendar.getTime());

                // Update the clock TextView with Arabic numerals
                clockTextView.setText(currentTime);

                // Call the runnable again after 1 second
                handler.postDelayed(this, 1000);
            }
        };

        // Start the clock by posting the runnable
        handler.post(runnable);



    }



    // Check if the app is in lock task mode
    private boolean isInLockTaskMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            return activityManager != null && activityManager.isInLockTaskMode();
        }
        return false;
    }




    private void launchApp(AppInfo appInfo) {
        PackageManager packageManager = getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, "App not found", Toast.LENGTH_SHORT).show();
        }
    }



}
