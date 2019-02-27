package com.example.android.mapsmvvm;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MyViewModel extends AndroidViewModel {
    private TrackingRepository repository;
    private LiveData<Location> locationLiveData;
    private LiveData<Boolean> trackingState;

    public MyViewModel(@NonNull Application application) {
        super(application);
        repository = TrackingRepository.getInstance(application);
        locationLiveData = repository.getLocationLiveData();
        trackingState = repository.getTrackingState();
    }

    public void requestLocationUpdates() {
        repository.requestLocationUpdates();
    }

    public void removeLocationUpdates() {
        repository.removeLocationUpdates();
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<Boolean> getTrackingState() {
        return trackingState;
    }
}
