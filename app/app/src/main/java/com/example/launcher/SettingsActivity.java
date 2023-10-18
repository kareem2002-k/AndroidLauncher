package com.example.launcher;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {
    private SeekBar brightnessSeekBar;
    private TextView brightnessTextView;
    private Switch wifiSwitch;
    private Switch bluetoothSwitch;
    private View cellularDataLayout;

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        brightnessTextView = findViewById(R.id.brightnessTextView);
        wifiSwitch = findViewById(R.id.wifiSwitch);
        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);
        cellularDataLayout = findViewById(R.id.cellularDataSwitch);

        // Get the current brightness level
        int currentBrightness = getBrightness();
        brightnessSeekBar.setProgress(currentBrightness);
        brightnessTextView.setText(getString(R.string.brightness_percentage, currentBrightness));

        brightnessSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the brightness level and text view
                setBrightness(progress);
                brightnessTextView.setText(getString(R.string.brightness_percentage, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed in this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed in this example
            }
        });

        wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle Wi-Fi switch toggle
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(isChecked);
        });

        bluetoothSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle Bluetooth switch toggle
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (isChecked) {
                enableBluetooth();
            } else {
                disableBluetooth();
            }
        });

        cellularDataLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            startActivity(intent);
        });

    }

    private int getBrightness() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void setBrightness(int brightness) {
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
    }

    private void disableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.disable();
    }
}
