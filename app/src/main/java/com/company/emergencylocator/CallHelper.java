package com.company.emergencylocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

public class CallHelper {

    MainActivity mMainActivity = new MainActivity();
    private final String TAG = "Call Helper";

    private Context ctx;
    private OutgoingReceiver mOutgoingReceiver;
    private LocationDetector mLocationDetector;
    // private TelephonyManager tm;
    // private CallStateListener callStateListener;

    public CallHelper(Context context) {
        ctx = context;
        // callStateListener = new CallStateListener();
        mOutgoingReceiver = new OutgoingReceiver();
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
        public OutgoingReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, "Outgoing phone number: " + number);
            Toast.makeText(context, "Outgoing: "+number, Toast.LENGTH_LONG).show();

            // mLocationDetector.sendLocation();
        }
    }

    public void start(Context context) {
        // Registration of the listener for incoming calls
        // tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        // tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // Registration of the listener for outgoing calls
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        ctx.registerReceiver(mOutgoingReceiver, intentFilter);

        mLocationDetector = new LocationDetector(context);
    }

    public void stop() {
        // tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        ctx.unregisterReceiver(mOutgoingReceiver);
    }
}
