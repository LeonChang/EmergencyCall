package com.company.emergencylocator;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class EmergencyService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = EmergencyService.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9999;
    private static final int PERMISSIONS_FINE_LOCATION = 9000;

    private static CallHelper callHelper;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Context ctx;
    private boolean sendLocation = false;
    private String mPhoneNumber;

    public EmergencyService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG, "Service created");

        // Get phone number
        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
        Log.i(TAG, "My phone number is " + mPhoneNumber);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callHelper = new CallHelper(this, this, mGoogleApiClient);
        ctx = this;

        int res = super.onStartCommand(intent, flags, startId);
        callHelper.start();
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        callHelper.stop();
        Log.i(TAG, "Service ended");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not supporting binding
        return null;
    }

    public static void changeEmergencyNumber(String emergencyNumber) {
        callHelper.changeEmergencyNumber(emergencyNumber);
    }

    public void activateSendLocation() {sendLocation = true;};

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected. Google Play services available: " + GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "No permissions in the onConnected() method!");
            // ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // Check if we have a recent location (within last 2 minutes)
        if (location != null && location.getTime() - Calendar.getInstance().getTimeInMillis() > 2 * 60 * 1000) {
            Log.i(TAG, "Location not null");
            onLocationChanged(location);
        }
        else {
            Log.i(TAG, "Location is null");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        };
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "We are in OnLocationChanged");
        if (sendLocation && location != null) {
            //TODO: Send location!
            Log.i(TAG, "Send our location: " + location.getLatitude() + " and " + location.getLongitude() + " with phone number " + mPhoneNumber);
            sendLocation = false;
        }

        // If we have a recent location, turn off the location updates
        if (location != null) {
            // Log.i(TAG, "Our location is: " + location.getLatitude() + " and " + location.getLongitude());
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                // connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
        Log.i(TAG, "Location services failed.");
    }


}