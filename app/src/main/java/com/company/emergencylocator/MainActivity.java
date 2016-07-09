package com.company.emergencylocator;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Emergency Locator Main Activity";
    private Button mCallButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCallButton = (Button) findViewById(R.id.callButton);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(getEmergencyNumber());
            }
        });

    }

    private String getEmergencyNumber() {
        return "9083926510";
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
}
