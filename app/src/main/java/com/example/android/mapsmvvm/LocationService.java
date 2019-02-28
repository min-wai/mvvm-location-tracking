package com.example.android.mapsmvvm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

public class LocationService extends LifecycleService {
    private static final int NOTIFICATION_ID = 6438;
    private static final String CHANNEL_ID = "foreground";
    private MyViewModel myViewModel;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopForeground(true);
//        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getMyNotification(""));
        myViewModel = new MyViewModel(getApplication());
        startObservingLocationLivedata();
        return START_STICKY;
    }

    private void startObservingLocationLivedata() {
        myViewModel.getLocationLiveData().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                updateNotification(location.toString());
            }
        });
    }

    private Notification getMyNotification(String message) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MapsActivity.class), 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Current Location")
                .setContentText(message)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setContentIntent(contentIntent)
                .build();
    }

    private void updateNotification(String message) {
        Notification notification = getMyNotification(message);
        NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManagerCompat.from(getApplicationContext()).createNotificationChannel(channel);
    }
}
