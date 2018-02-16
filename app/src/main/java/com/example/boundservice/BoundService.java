package com.example.boundservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Chronometer;

/**
 * Created by usuario on 16/02/18.
 */

public class BoundService extends Service {
    private Chronometer chronometer;

    @Override
    public void onCreate() {
        super.onCreate();
        chronometer = new Chronometer(this);
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        chronometer.start();
        //No sticky esque no va a ser infinito. Si es sticky es que el
        //servicio está destinado a ser infinito.
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * El servicio ofrece la hora actual a una Activity
     * @return
     */
    public String getTimestamp() {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        int hour = (int) (elapsedMillis/3600000);
        int minute = (int) (elapsedMillis-hour*3600000)/60000;
        int second = (int)(elapsedMillis-hour*3600000-minute*60000)/1000;
        int millis = (int)(elapsedMillis-hour*3600000-minute*60000-second*1000)/1000;
        return hour+":"+minute+":"+second+":"+millis;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
