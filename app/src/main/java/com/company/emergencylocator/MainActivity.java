package com.company.emergencylocator;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
 {

    private static final String TAG = "EmergencyLocator Main";

    private Button mCallButton;
    private Button mEndServiceButton;
    private EditText mEmergencyNumberEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCallButton = (Button) findViewById(R.id.callButton);
        mEndServiceButton = (Button) findViewById(R.id.endServiceButton);
        mEmergencyNumberEditor = (EditText) findViewById(R.id.NumberField);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(getEmergencyNumber());
            }
        });



        final Intent intent = new Intent(this, EmergencyService.class);
        if (!isMyServiceRunning(EmergencyService.class)) {
            startService(intent);
        }

        mEndServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(intent);
            }
        });
        mEmergencyNumberEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EmergencyService.changeEmergencyNumber(mEmergencyNumberEditor.getText().toString());
            }
        });
    }

    private String getEmergencyNumber() {
        return mEmergencyNumberEditor.getText().toString();
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
        try{
            startActivity(intent);
        }
        catch (Exception e) {
            Log.i(TAG, "The makeCall() method didn't work, with exception: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Map on resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
