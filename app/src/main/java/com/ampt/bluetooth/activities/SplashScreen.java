package com.ampt.bluetooth.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.ampt.bluetooth.R;

/**
 * Created by Heshanr on 4/12/2015.
 */
public class SplashScreen extends Activity {
    /**
     * The thread to process splash screen events
     */
    private Thread mSplashThread;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Splash screen view
        setContentView(R.layout.splash);

        final SplashScreen sPlashScreen = this;

        // The thread to wait for splash screen events

        mSplashThread =  new Thread(){
            boolean on = true;
            @Override
            public void run(){
                if(on) {
                    try {
                        synchronized (this) {
                            // Wait given period of time or exit on touch
                            wait(2000);
                        }
                    } catch (InterruptedException ex) {
                    }

                    finish();

                    // Run next activity
                    Intent intent = new Intent();
                    intent.setClass(sPlashScreen, LoginActivity.class);
                    startActivity(intent);
                    on = false;
                }
            }
        };

        mSplashThread.start();
    }

    /**
     * Processes splash screen touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;
    }
}
