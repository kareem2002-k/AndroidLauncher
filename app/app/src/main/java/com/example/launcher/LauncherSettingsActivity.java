package com.example.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;


public class LauncherSettingsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_settings);

        System.out.println("hi");

        // Handle user interaction to set your app as the default launcher
        Button setLauncherButton = findViewById(R.id.setLauncherButton);
        setLauncherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaultLauncher();
            }
        });

        // Check for SET_PREFERRED_APPLICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission("android.permission.SET_PREFERRED_APPLICATIONS") != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it.
                requestPermissions(new String[]{"android.permission.SET_PREFERRED_APPLICATIONS"}, PERMISSION_REQUEST_CODE);
                return;
            }
        }
    }

    private void setDefaultLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setPackage(getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                setDefaultLauncher();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
