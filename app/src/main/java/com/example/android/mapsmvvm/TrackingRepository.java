package com.example.android.mapsmvvm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TrackingRepository {
    private static TrackingRepository ourInstance;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> trackingState = new MutableLiveData<>();

    public static TrackingRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new TrackingRepository(context.getApplicationContext());
        }

        return ourInstance;
    }

    @SuppressLint("MissingPermission")
    private TrackingRepository(Context applicationContext) {
        // Init
        trackingState.setValue(false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext);
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        locationLiveData.setValue(location);
                    }
                });
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(12 * 1000)
                .setFastestInterval(10 * 1000);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                locationLiveData.setValue(locationResult.getLastLocation());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<Boolean> getTrackingState() {
        return trackingState;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        removeLocationUpdates();
        trackingState.setValue(true);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    public void removeLocationUpdates() {
        trackingState.setValue(false);
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }
}
