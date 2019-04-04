package com.z.zz.zzz.antidetector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.z.zz.zzz.AntiDetector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AntiDetector.create(this)
                .setDebug(BuildConfig.DEBUG)
                .setSticky(true)
                .detect(new AntiDetector.OnDetectorListener() {
                    @Override
                    public void onResult(boolean result, int flag) {
                        Log.i("Main", "AntiDetector result: " + result + ", flag: " + Integer.toBinaryString(flag));
                    }
                });
    }
}
