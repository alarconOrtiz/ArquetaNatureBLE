package es.alarcon.arquetanatureble.GUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;


import es.alarcon.arquetanatureble.BEAN.BeanBluetoothDevice;
import es.alarcon.arquetanatureble.BLE.HandlerBLE;
import es.alarcon.arquetanatureble.BroadCastReceiver.BLEBroadcastReceiver;
import es.alarcon.arquetanatureble.CORE.ServiceDetectionTag;

import es.alarcon.arquetanatureble.CORE.BLE_Application;
import es.alarcon.arquetanatureble.R;
import es.alarcon.arquetanatureble.UTIL.Constant;


public class ScanActivity extends ListActivity implements OnItemClickListener{ //}, LeScanCallback {

    private static final int SCAN_ITEM = 1;
    private static ListView mListView;
    private static Context mContext;
    private static HandlerBLE mHandlerBLE;
    private Handler mHandler;
    private static ScanArrayAdapter mAdapter;
    private static List<BeanBluetoothDevice> mDeviceList;
    private Menu mMenu;
    private static Activity mActivity;
    private BLEBroadcastReceiver mScanBroadcastReceiver;
    public static boolean isOncreate = false;


    //###################################################################
    /****************** metodos del flujo Android. **********************/
    //###################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        isOncreate = true;

        mActivity   = this;
        mContext    = this;
        mHandler    = new Handler() ;
        mDeviceList = new ArrayList<BeanBluetoothDevice>();
        mAdapter    = new ScanArrayAdapter(this, mDeviceList);

        //manejador BLE
        mHandlerBLE = ((BLE_Application) getApplication()).getmHandlerBLEInstance(this);
        ((BLE_Application) getApplication()).resetHandlerBLE();

        mScanBroadcastReceiver = new BLEBroadcastReceiver(this, mAdapter);
        //mScanBroadcastReceiver = new BLEBroadcastReceiver();

        //IntentFilter i = new IntentFilter(HandlerBLE.ACTION_DEVICE_ADVERTISING);
        //registerReceiver(mScanBroadcastReceiver, i);

        //run service
        //Intent service = new Intent(this, ServiceDetectionTag.class);
        //startService(service);


        //checking if blue is enable.
        if(mHandlerBLE.IsEnabledBlue())
        {
            Intent enableBlue = new Intent(mHandlerBLE.getBlueAdapter().ACTION_REQUEST_ENABLE);
            startActivity(enableBlue);
            finish();
            return;
        }

        mListView = getListView();
        mListView.setVisibility(View.VISIBLE);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        mMenu = menu;
        String menuTitle= getResources().getString(R.string.scan);
        menu.add(0,SCAN_ITEM,0,menuTitle);

       /*
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case SCAN_ITEM:
                scan();
                break;
        }
        return true;
        /*
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);*/
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "BLE TECHNOLOGY NOT SUPPORTED ON THIS DEVICE", Toast.LENGTH_SHORT).show();
            finish();
        }

        //isOncreate = false;
        mAdapter.clear();
        HandlerBLE.setup();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onPause() {
        super.onPause();
        mListView.setOnClickListener(null);
        //Make sure that there is no pending Callback
        mHandler.removeCallbacks(mStopRunnable);

        //stop service
        //Intent service = new Intent(this, ServiceDetectionTag.class);
        //stopService(service);

        //if(mScanBroadcastReceiver != null)
        //    unregisterReceiver(mScanBroadcastReceiver);

        mAdapter.clear();

        isOncreate = false;

        if (mHandlerBLE.isScanning) {
            mHandlerBLE.stopLeScan();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //stop service
        //Intent service = new Intent(this, ServiceDetectionTag.class);
        //stopService(service);

        isOncreate = false;

    }

    //###################################################################
    /****************** metodos manejo Tag        **********************/
    //###################################################################
    @Override
    //recogemos los metodos del tag seleccionado y recogemos los datos.
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
         /*
        //stop service
        Intent service = new Intent(this, ServiceDetectionTag.class);
        stopService(service);
        */

        //unregisterReceiver(mScanBroadcastReceiver);

        if (Constant.DEBUG)
            Log.i(Constant.TAG, "Selected device " + mDeviceList.get(position).getBluetoothDevice().getAddress());

        if (mHandlerBLE.isScanning)
        { //stop scanning
            configureScan(false);
            mHandlerBLE.stopLeScan();
            if (Constant.DEBUG)
                Log.i(Constant.TAG, "Stop scanning");
        }

        String address  = mDeviceList.get(position).getBluetoothDevice().getAddress();
        String name     = mDeviceList.get(position).getBluetoothDevice().getName();

        if (name==null)
            name="unknown";

        Intent intentActivity= new Intent(this, DeviceActivity.class);
        intentActivity.putExtra(Constant.EXTRA_ADDRESS, address);
        intentActivity.putExtra(Constant.EXTRA_NAME, name);
        this.startActivity(intentActivity);

    }

    //Handle automatic stop of LEScan
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            mHandlerBLE.stopLeScan();
            configureScan(false);
            if (Constant.DEBUG)
                Log.i(Constant.TAG, "Stop scanning");
        }
    };

    public void configureScan(boolean flag)
    {
        //isScanning      = flag;
        String itemText = null;

        if (mHandlerBLE.isScanning)
        {
            itemText = getResources().getString(R.string.stopScan);
            mHandlerBLE.stopLeScan();
            if (Constant.DEBUG)
                Log.i(Constant.TAG, "ScanActivity -- StopScan");
        }
        else
        {
            itemText= getResources().getString(R.string.scan);
            mHandlerBLE.startLeScan();

            if (Constant.DEBUG)
                Log.i(Constant.TAG, "ScanActivity -- StartScan");
        }

        mMenu.findItem(SCAN_ITEM).setTitle(itemText);

        if (Constant.DEBUG)
            Log.i(Constant.TAG, "Updated Menu Item. New value: " + itemText);
    }

    // Metodo para iniciar el scaneo cuando te llaman manualmente.
    private void scan() {
        if (mHandlerBLE.isScanning) { //stop scanning
            configureScan(false);
            mHandlerBLE.stopLeScan();

            if (Constant.DEBUG)
                Log.i(Constant.TAG, "Stop scanning");

            return;
        } else {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            configureScan(true);

            if (Constant.DEBUG)
                Log.i(Constant.TAG, "Start scanning for BLE devices...");

            mHandlerBLE.startLeScan();
            //automatically stop LE scan after 5 seconds
            mHandler.postDelayed(mStopRunnable, 30000);
        }
    }


    /*
    Clase para crear el adaptador de dispositos Bluetooh
     */
   /* public class MySimpleArrayAdapter extends ArrayAdapter {
        private final Context context;

        public MySimpleArrayAdapter(Context context, List<BluetoothDevice> deviceList)
        {
            super(context, R.layout.activity_scan_item,R.id.deviceName, deviceList);
            this.context = context;
        }
    }*/

    public static ScanActivity GetScanActivity()
    {
        return (ScanActivity) mActivity;
    }

    public void addNewDevice2MySimpleArrayAdapter(BeanBluetoothDevice beanDeviceFound) {

        final BluetoothDevice deviceFound = beanDeviceFound.getBluetoothDevice();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.add(deviceFound);
                        mAdapter.notifyDataSetChanged();

                        if (Constant.DEBUG)
                            Log.i(Constant.TAG, "ScanActivity -- addNewDevice2MySimpleArrayAdapter() -> Added new device "
                                    + deviceFound.getAddress() + " --- Name: " + deviceFound.getName());
                    }
                });
            }
        });
    }
    /*
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
    {
        String name="unknown";

        if (device.getName()!=null)
            name=device.getName();

        final String finalName = name;
        final String  finalAddress = device.getAddress();

        if (Constant.DEBUG)
            Log.i(Constant.TAG, "Found new device "+ finalAddress + " --- Name: " + finalName);

        final BluetoothDevice finalDevice= device;
        // This callback from Bluetooth LEScan can arrive at any moment not necessarily on UI thread.
        // Use this mechanism to update list View
        mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.add(finalDevice);
                                        mAdapter.notifyDataSetChanged();

                                        if (Constant.DEBUG)
                                            Log.i(Constant.TAG, "Added new device "+ finalAddress + " --- Name: " + finalName);
                                    }
                                }
        );
    }*/

}
