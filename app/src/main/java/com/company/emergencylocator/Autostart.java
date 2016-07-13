package com.company.emergencylocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Autostart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent CallDetectServiceIntent = new Intent(context, EmergencyService.class);
        context.startService(CallDetectServiceIntent);
        Log.i("Autostart", "Autostart has activated");
    }
}
