package com.s23010372.hirudini;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "TemperatureSensor";
    private static final float TEMPERATURE_THRESHOLD = 72.0f; // SID last two digits

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TextView tvTemperatureValue, tvThresholdValue, tvTemperatureStatus, tvSensorInfo;
    private Button btnSimulate;
    private MediaPlayer mediaPlayer;
    private boolean isAudioPlaying = false;

    private void startSimulatedTemperatureLoop() {
        new Thread(() -> {
            while (true) {
                runOnUiThread(this::simulateTemperature);
                try {
                    Thread.sleep(5000); // simulate every 5 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Initialize UI
        tvTemperatureValue = findViewById(R.id.tvTemperatureValue);
        tvThresholdValue = findViewById(R.id.tvThresholdValue);
        tvTemperatureStatus = findViewById(R.id.tvTemperatureStatus);
        tvSensorInfo = findViewById(R.id.tvSensorInfo);
        btnSimulate = findViewById(R.id.btnSimulate);

        tvThresholdValue.setText(TEMPERATURE_THRESHOLD + "°C");

        // Sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (temperatureSensor != null) {
            tvSensorInfo.setText("Hardware temperature sensor detected.");
            tvTemperatureStatus.setText("Sensor Active");
            tvTemperatureStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvSensorInfo.setText("No temperature sensor available - Use simulation.");
            tvTemperatureStatus.setText("Simulation Mode");
            tvTemperatureStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

            // Auto-simulate temperature every few seconds
            startSimulatedTemperatureLoop();
        }


        // MediaPlayer for alert
        try {
            mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing MediaPlayer: " + e.getMessage());
        }

        // Simulate button
        btnSimulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateTemperature();
            }
        });
    }

    private void simulateTemperature() {
        float[] testTemps = {90.0f, 20.0f, 36.0f, 68.0f, 75.0f, 70.0f, 80.0f};
        int idx = (int) (Math.random() * testTemps.length);
        float simulatedTemp = testTemps[idx];
        updateTemperatureDisplay(simulatedTemp);
        Toast.makeText(this, "Simulated: " + simulatedTemp + "°C", Toast.LENGTH_SHORT).show();
    }

    private void updateTemperatureDisplay(float temperature) {
        tvTemperatureValue.setText(String.format("%.1f°C", temperature));
        if (temperature > TEMPERATURE_THRESHOLD) {
            tvTemperatureStatus.setText("ALERT: Above Threshold");
            tvTemperatureStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            if (!isAudioPlaying) playAlertSound();
        } else {
            String status = temperatureSensor != null ? "Sensor Active" : "Simulation Mode";
            tvTemperatureStatus.setText(status + " - Normal");
            tvTemperatureStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            isAudioPlaying = false;
        }
    }

    private void playAlertSound() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.prepare();
                }
                mediaPlayer.start();
                isAudioPlaying = true;

// Reset flag after sound ends
                mediaPlayer.setOnCompletionListener(mp -> isAudioPlaying = false);

                isAudioPlaying = true;
                Toast.makeText(this, "Temperature Alert - Playing audio", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error playing alert sound: " + e.getMessage());
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            updateTemperatureDisplay(temperature);
            Log.d(TAG, "Hardware sensor reading: " + temperature + "°C");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not required for this lab
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isAudioPlaying = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}