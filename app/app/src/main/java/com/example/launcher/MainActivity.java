package com.example.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.content.BroadcastReceiver;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private List<AppInfo> appInfoList;
    private RecyclerView recyclerView;
    private AppAdapter appAdapter;

    private TextView clockTextView;
    private TextView batteryTextView;
    private TextView networkTextView;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);







        // Initialize TextViews for custom status bar
        batteryTextView = findViewById(R.id.batteryTextView);
        networkTextView = findViewById(R.id.networkTextView);
        timeTextView = findViewById(R.id.timeTextView);

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


        // Register a BroadcastReceiver to monitor battery status
        registerReceiver(new BatteryReceiver(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // Register a BroadcastReceiver to monitor network status
        registerReceiver(new NetworkReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }




    // Check if the app is in lock task mode


    private void launchApp(AppInfo appInfo) {
        PackageManager packageManager = getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
        if (launchIntent != null) {
            // Launch the selected app
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, "App not found", Toast.LENGTH_SHORT).show();
        }
    }



    // BroadcastReceiver for monitoring battery status
    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Calculate battery percentage
            int batteryPercentage = (int) ((level / (float) scale) * 100);


            // Update the battery TextView
            batteryTextView.setText("Battery: " + batteryPercentage + "%");
        }
    }

    // BroadcastReceiver for monitoring network status
    private class NetworkReceiver extends BroadcastReceiver {
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

        // Check if the device is connected to Wi-Fi
        private boolean isConnectedToWifi(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            }
            return false;
        }

        // Check if the device is connected to mobile data
        private boolean isConnectedToMobileData(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
            return false;
        }




    }


    public class CustomViewGroup extends ViewGroup {

        private boolean interceptTouch = true;



        public void setInterceptTouch(boolean interceptTouch) {
            this.interceptTouch = interceptTouch;
        }


        public CustomViewGroup(Context context) {
            super(context);
        }

        public CustomViewGroup(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            // Implement layout logic for your custom view
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            // Capture and handle touch events here
            if (interceptTouch) {
                return true; // Consume the touch event to prevent it from reaching the status bar
            }

            // Return false to allow the touch event to continue and potentially reach the status bar
            return false;
        }
    }





}
