package com.example.android.mapsmvvm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MyViewModel myViewModel;
    private Marker marker;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        button = findViewById(R.id.serviceBtn);
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        confirmPermissionAndGetMap(mapFragment);
        setUpButtonReaction();
    }

    private void setUpButtonReaction() {
        myViewModel.getTrackingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRunning) {
                if (isRunning) {
                    button.setText("Stop");
                } else {
                    button.setText("Start");
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myViewModel.getTrackingState().getValue()) {
                    stopLocationUpdates();
                } else {
                    startLocationUpdates();
                }
            }
        });
    }

    private void startLocationUpdates() {
        myViewModel.requestLocationUpdates();
        startForegroundService();
    }

    private void stopLocationUpdates() {
        marker.remove();
        marker = null;
        myViewModel.removeLocationUpdates();
        stopForegroundService();
    }

    private void stopForegroundService() {
        stopService(new Intent(this, LocationService.class));
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, LocationService.class));
        } else {
            startService(new Intent(this, LocationService.class));
        }
    }

    private void observeLocationLiveData() {
        myViewModel.getLocationLiveData().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (marker == null) {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Nibba"));
                } else {
                    marker.setPosition(latLng);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        observeLocationLiveData();
    }

    private void confirmPermissionAndGetMap(final SupportMapFragment mapFragment) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(MapsActivity.this, "Location Permissions Granted", Toast.LENGTH_SHORT).show();
                            mapFragment.getMapAsync(MapsActivity.this);
                        } else {
                            Toast.makeText(MapsActivity.this, "Location Permissions needed to use this app", Toast.LENGTH_SHORT);
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }
}
