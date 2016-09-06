package com.company.emergencylocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class CallHelper {

    MainActivity mMainActivity = new MainActivity();
    private final String TAG = "Call Helper";

    private Context ctx;
    private OutgoingReceiver outgoingReceiver;
    private static String mEmergencyNumber = "911";
    // private TelephonyManager tm;
    // private CallStateListener callStateListener;

    public CallHelper(EmergencyService service, Context context, GoogleApiClient client) {
        ctx = context;
        // callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver(service, ctx, client);
    }

    public static void changeEmergencyNumber(String emergencyNumber) {
        mEmergencyNumber = emergencyNumber;
    }


    /* Incoming Calls
    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged (int state, String incomingNumber) {
            switch(state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "Incoming phone number: " + incomingNumber);
//                    Toast.makeText(this, "Incoming: "+incomingNumber, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    } */

    // Outgoing Calls
    private class OutgoingReceiver extends BroadcastReceiver {
        private GoogleApiClient ReceiverGoogleApiclient;
        private Context mContext;
        private EmergencyService mEmergencyService;

        public OutgoingReceiver(EmergencyService service, Context context, GoogleApiClient client) {
            mEmergencyService = service;
            mContext = context;
            ReceiverGoogleApiclient = client;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            //Log.i(TAG, "Outgoing phone number: " + number);
            if (number.equalsIgnoreCase(mEmergencyNumber)) {
                Log.i(TAG, "Outgoing emergency number: " + number);
                ReceiverGoogleApiclient.connect();
                mEmergencyService.activateSendLocation();

            }
        }
    }


    public void start() {
        // Registration of the listener for incoming calls
        // tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        // tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // Registration of the listener for outgoing calls
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        ctx.registerReceiver(outgoingReceiver, intentFilter);
    }

    public void stop() {
        // tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        ctx.unregisterReceiver(outgoingReceiver);
    }
}
