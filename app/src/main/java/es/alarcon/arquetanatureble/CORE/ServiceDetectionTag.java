package es.alarcon.arquetanatureble.CORE;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import es.alarcon.arquetanatureble.BLE.HandlerBLE;
import es.alarcon.arquetanatureble.UTIL.Constant;


/**
 * Created by alarcon on 4/7/15.
 */
public class ServiceDetectionTag extends IntentService {
    private static final String NAMECLASS = "ServiceDetectionTag";
    private static final long STOP_SCAN_TIMER = 10 * 1000;
    private static final long START_SCAN_TIMER = 30 * 1000;
    private static boolean isScanning;

    private HandlerBLE mHandlerBLE;
    private ServiceDetectionTag mServiceDetectionTag;
    private ServiceBroadcastReceiver mServiceBroadcastReceiver;
    private static Thread workerThread = null;

    public ServiceDetectionTag() {
        super(NAMECLASS);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        mHandlerBLE = ((BLE_Application) getApplication()).getmHandlerBLEInstance(this.getApplicationContext());
        ((BLE_Application) getApplication()).resetHandlerBLE();


        mServiceBroadcastReceiver = new ServiceBroadcastReceiver();
        IntentFilter i = new IntentFilter(ServiceBroadcastReceiver.ACTION_NOTIFY_NEW_DEVICE_FOUND);
        registerReceiver(mServiceBroadcastReceiver, i);

        if(workerThread == null || !workerThread.isAlive())
        {
            workerThread = new Thread(new Runnable()
            {
                public void run()
                {

                    Timer mTimerStart = new Timer();
                    Timer mTimerStop = new Timer();

                    mTimerStart.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run()
                        {
                            if (Constant.DEBUG)
                                Log.i(Constant.TAG, NAMECLASS + "-- onHandleIntent -> Start scanning");
                            mHandlerBLE.startLeScan();
                        }
                    }, 0, START_SCAN_TIMER + STOP_SCAN_TIMER);

                    mTimerStop.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run()
                        {
                            if (Constant.DEBUG)
                                Log.i(Constant.TAG, NAMECLASS + "-- onHandleIntent -> Stop scanning");
                            mHandlerBLE.stopLeScan();
                        }
                    }, 0, START_SCAN_TIMER);
                }
            });
            workerThread.start();
        }
    }

    @Override
    public void onDestroy() {
        if (Constant.DEBUG)
            Log.i(Constant.TAG, NAMECLASS + " -- onDestroy() -> Stop scanning");

        mHandlerBLE.stopLeScan();
        unregisterReceiver(mServiceBroadcastReceiver);

        super.onDestroy();
    }

    public class ServiceBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_NOTIFY_NEW_DEVICE_FOUND = "pfc.teleco.upct.es.iot_ble.NOTIFY_NEW_DEVICE";

        public ServiceBroadcastReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.DEBUG)
                Log.i(Constant.TAG, "ServiceBroadcastReceiver -- onReceive -> inside");
            String action = intent.getAction();
            if (action.equals(ServiceBroadcastReceiver.ACTION_NOTIFY_NEW_DEVICE_FOUND)) {
                if (Constant.DEBUG)
                    Log.i(Constant.TAG, "ServiceBroadcastReceiver -- onReceive -> sending notication(statusBar)");


                MyNotificationHandler myNotificationHandler;
                myNotificationHandler = new MyNotificationHandler(mServiceDetectionTag);
                myNotificationHandler.SendNotify();
            }
        }

    }
/*
    @Override
    public void onCreate() {
        super.onCreate();

        mServiceDetectionTag = this;

        mHandlerBLE = ((BLE_Application) getApplication()).getmHandlerBLEInstance(this.getApplicationContext());
        ((BLE_Application) getApplication()).resetHandlerBLE();

        mServiceBroadcastReceiver = new ServiceBroadcastReceiver();
        IntentFilter i = new IntentFilter(ServiceBroadcastReceiver.ACTION_NOTIFY_NEW_DEVICE_FOUND);
        registerReceiver(mServiceBroadcastReceiver, i);

        mHandler = new Handler();
        mTimer = new Timer(true);

        Timer mTimer2 = new Timer();

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (Constant.DEBUG)
                    Log.i(Constant.TAG, NAMECLASS + " -- Start scanning");
                mHandlerBLE.startLeScan();
                isScanning = true;
            }
        }, 0, SCAN_TIMER);
    }


    //Handle automatic stop of LEScan
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            mHandlerBLE.stopLeScan();
            isScanning = false;
            if (Constant.DEBUG)
                Log.i(Constant.TAG, NAMECLASS+" Stop scanning");
        }
    };


    @Override
    public void onStart(Intent intent, int startid) {

    }


    @Override
    public void onDestroy() {
        if (Constant.DEBUG)
            Log.i(Constant.TAG, NAMECLASS + " -- onDestroy() -> Stop scanning");

        mHandlerBLE.stopLeScan();
        unregisterReceiver(mServiceBroadcastReceiver);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public class ServiceBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_NOTIFY_NEW_DEVICE_FOUND = "pfc.teleco.upct.es.iot_ble.NOTIFY_NEW_DEVICE";

        public ServiceBroadcastReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.DEBUG)
                Log.i(Constant.TAG, "ServiceBroadcastReceiver -- onReceive -> inside");
            String action = intent.getAction();
            if (action.equals(ServiceBroadcastReceiver.ACTION_NOTIFY_NEW_DEVICE_FOUND)) {
                if (Constant.DEBUG)
                    Log.i(Constant.TAG, "ServiceBroadcastReceiver -- onReceive -> sending notication(statusBar)");


                MyNotificationHandler myNotificationHandler;
                myNotificationHandler = new MyNotificationHandler(mServiceDetectionTag);
                myNotificationHandler.SendNotify();
            }
        }

    }*/
}
