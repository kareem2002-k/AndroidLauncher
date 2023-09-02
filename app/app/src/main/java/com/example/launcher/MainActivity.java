package com.example.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppAdapter appAdapter;
    private List<AppInfo> appInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        for (ResolveInfo info : infoList) {
            AppInfo appInfo = new AppInfo();
            appInfo.setLabel(info.loadLabel(packageManager).toString());
            appInfo.setPackageName(info.activityInfo.packageName);
            appInfo.setIcon(info.loadIcon(packageManager));

            appInfoList.add(appInfo);
        }

        // Add Google Chrome if not already included

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
