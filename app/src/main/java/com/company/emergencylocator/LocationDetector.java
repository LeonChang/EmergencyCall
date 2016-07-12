package com.company.emergencylocator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Calendar;

public class LocationDetector implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationDetector.class.getSimpleName();
    private static final int PERMISSIONS_FINE_LOCATION = 9999;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private Context mContext;


    public LocationDetector(Context context) {
        Log.i(TAG, "In LocationDetector constructor");
        mContext = context;

        // Google API
        new GoogleApiClient.Builder(mContext);
        /*        .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
*/

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    /*
    public void sendLocation() {
        String apiKey = "AIzaSyAiLKkEtWqBoKhRqbWa190Zf8OmBlkg7Ek";
        String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + mLocation.getLatitude() + "," + mLocation.getLongitude() + "&key=" + apiKey;

        if (isNetworkAvailable() && mLocation != null) {
            // toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(geocodeUrl)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // toggleRefresh();
                    // alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // toggleRefresh();
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            JSONArray geocode = new JSONObject(jsonData).getJSONArray("results");
                            JSONObject geocodeObject = geocode.getJSONObject(3);
                            final String address = geocodeObject.getString("formatted_address");
                            Log.i(TAG, "Our address is " + address);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // mLocationLabel.setText(address);
                                }
                            });
                        } else {
                            // alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Network or location unavailable", Toast.LENGTH_LONG).show();
        }

    } */

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected. Google Play services available: " + GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext));

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "No permissions in the onConnected() method!");
            ActivityCompat.requestPermissions(MainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
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
        // If we have a recent location, turn off the location updates
        if (location != null) {
            Log.i(TAG, "Our location is: " + location.getLatitude() + " and " + location.getLongitude());
            mLocation = location;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Location services failed. Please reconnect.");
    }
}
