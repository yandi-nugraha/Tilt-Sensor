package com.example.tiltsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Pertama
    private SensorManager sensorManager;

    private Sensor sensorAccelerometer, sensorMagnetometer;

    private TextView textSensorAzimuth, textSensorPitch, textSensorRoll;

    private float[] accelerometerData = new float[3], magnetometerData = new float[3];

    // Kedua
    private ImageView spotTop, spotBottom, spotRight, spotLeft;

    private static final float VALUE_DRIFT = 0.0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pertama
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textSensorAzimuth = findViewById(R.id.value_azimuth);
        textSensorPitch = findViewById(R.id.value_pitch);
        textSensorRoll = findViewById(R.id.value_roll);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Kedua
        spotTop = findViewById(R.id.spot_top);
        spotBottom = findViewById(R.id.spot_bottom);
        spotRight = findViewById(R.id.spot_right);
        spotLeft = findViewById(R.id.spot_left);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensorAccelerometer != null) {
            sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorMagnetometer != null) {
            sensorManager.registerListener(this, sensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }

        float[] rotationMatrix = new float[9];
        boolean rotationOk = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);
        float orientationValues [] = new float[3];
        if (rotationOk) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        textSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));
        textSensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        textSensorRoll.setText(getResources().getString(R.string.value_format, roll));

        // Kedua
        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }
        spotTop.setAlpha(0.0f);
        spotBottom.setAlpha(0.0f);
        spotRight.setAlpha(0.0f);
        spotLeft.setAlpha(0.0f);

        if (pitch > 0) {
            spotBottom.setAlpha(pitch);
        } else {
            spotTop.setAlpha(Math.abs(pitch));
        }
        if (roll > 0) {
            spotLeft.setAlpha(roll);
        } else {
            spotRight.setAlpha(Math.abs(roll));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}