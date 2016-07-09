package com.company.emergencylocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallHelper {

    MainActivity mMainActivity = new MainActivity();
    private final String TAG = "Call Helper";

    private Context ctx;
    private OutgoingReceiver outgoingReceiver;
    // private TelephonyManager tm;
    // private CallStateListener callStateListener;

    public CallHelper(Context context) {
        ctx = context;
        // callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();
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
