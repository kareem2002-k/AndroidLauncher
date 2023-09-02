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




import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// ...

public class MainActivity extends AppCompatActivity {

    private List<AppInfo> appInfoList;
    private RecyclerView recyclerView;
    private AppAdapter appAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            // Check if the app is allowed (system app)
            if (allowedSystemApps.contains(packageName)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setLabel(info.loadLabel(packageManager).toString());
                appInfo.setPackageName(packageName); // Use the package name directly
                appInfo.setIcon(info.loadIcon(packageManager));

                appInfoList.add(appInfo);
            }
        }

        appAdapter.notifyDataSetChanged();
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
