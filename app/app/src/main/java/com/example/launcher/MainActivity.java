package com.example.launcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.content.Context;
import android.net.ConnectivityManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.widget.ProgressBar;
import android.view.View;



public class MainActivity extends AppCompatActivity {

    private List<AppInfo> appInfoList;
    private RecyclerView recyclerView;
    private AppAdapter appAdapter;

    private TextView clockTextView;
    private TextView batteryTextView;
    private TextView networkTextView;
    private TextView timeTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize other UI elements
        batteryTextView = findViewById(R.id.batteryTextView);
        networkTextView = findViewById(R.id.networkTextView);
        timeTextView = findViewById(R.id.timeTextView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        appInfoList = new ArrayList<>();
        appAdapter = new AppAdapter(appInfoList, new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppInfo appInfo) {
                if (appInfo.getPackageName().equals("com.example.launcher.settings_activity")) {
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                } else {
                    launchApp(appInfo);
                }
            }
        });

        recyclerView.setAdapter(appAdapter);

        // Initialize the clock TextView
        clockTextView = findViewById(R.id.clockTextView);


        AppInfo settingsAppInfo = new AppInfo("Settings", "com.example.launcher.settings_activity", getDrawable(android.R.drawable.sym_def_app_icon));
        appInfoList.add(settingsAppInfo);

// Create an AsyncTask to simulate a delay and fetch app package names
        new LoadAppsTask().execute();


        // Register a BroadcastReceiver to monitor battery status
        registerReceiver(new BatteryReceiver(batteryTextView), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // Register a BroadcastReceiver to monitor network status
        registerReceiver(new NetworkReceiver(networkTextView), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    // AsyncTask to simulate a delay before fetching app package names
    private class LoadAppsTask extends AsyncTask<Void, Void, List<AppInfo>> {

        ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);

        @Override
        protected void onPreExecute() {
            // Display the progress bar before loading
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<AppInfo> doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return fetchAppPackageNames();
        }



        @Override
        protected void onPostExecute(List<AppInfo> newAppInfoList) {
            loadingProgressBar.setVisibility(View.GONE);
            if (newAppInfoList != null) {
                // Clear the existing appInfoList
                appInfoList.clear();

                // Add the new appInfoList to the existing list
                appInfoList.addAll(newAppInfoList);

                // Notify the adapter that the data has changed
                appAdapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Done fetching data", Toast.LENGTH_SHORT).show();

            } else {


                // Handle the case where fetching failed
                Toast.makeText(MainActivity.this, "Failed to fetch app data", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private List<AppInfo> fetchAppPackageNames() {
        Set<String> uniquePackageNames = new HashSet<>();
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infoList = packageManager.queryIntentActivities(intent, 0);

        Set<String> allowedSystemApps = new HashSet<>(Arrays.asList(
                "com.android.camera",
                "com.android.camera2",
                "com.google.android.apps.maps",
                "com.google.android.apps.photos"
        ));

        AppInfo settingsAppInfo = new AppInfo("Settings", "com.example.launcher.settings_activity", getDrawable(android.R.drawable.sym_def_app_icon));
        appInfoList.add(settingsAppInfo);

        for (ResolveInfo info : infoList) {
            String packageName = info.activityInfo.packageName;

            if (allowedSystemApps.contains(packageName) && uniquePackageNames.add(packageName)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setLabel(info.loadLabel(packageManager).toString());
                appInfo.setPackageName(packageName);
                appInfo.setIcon(info.loadIcon(packageManager));

                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
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
