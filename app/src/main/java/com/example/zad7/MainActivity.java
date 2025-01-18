package com.example.zad7;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// MainActivity.java
public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button gpsButton;
    private LocationManager locationManager;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView providerText;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        playButton = findViewById(R.id.playButton);
        gpsButton = findViewById(R.id.gpsButton);
        latitudeText = findViewById(R.id.latitudeText);
        longitudeText = findViewById(R.id.longitudeText);
        providerText = findViewById(R.id.providerText);

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.muzyka);

        // Set up audio playback
        playButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setText("Play");
            } else {
                mediaPlayer.start();
                playButton.setText("Pause");
            }
        });

        // Set up GPS button
        gpsButton.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                startLocationUpdates();
            } else {
                requestLocationPermission();
            }
        });

        // Initialize LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, // Update every 1 second
                    0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            updateLocationInfo(location);
                        }

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}
                    }
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateLocationInfo(Location location) {
        latitudeText.setText("Szerokość: " + location.getLatitude());
        longitudeText.setText("Długość: " + location.getLongitude());
        providerText.setText("Dostawca: " + location.getProvider());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
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