package es.alarcon.arquetanatureble.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import es.alarcon.arquetanatureble.BEAN.BeanBluetoothDevice;
import es.alarcon.arquetanatureble.UTIL.Constant;


/**
 * Created by alarcon on 4/7/15.
 */

public class HandlerBLE implements LeScanCallback
{
    public static final String ACTION_DEVICE_ADVERTISING      = "es.alarcon.arquetanatureble.DEVICE_FOUND";
    public static final String ACTION_DEVICE_ADVERTISING_DATA = "es.alarcon.arquetanatureble.ADVERTISING_DATA";

    public static final int SELECT_ACTION_DEVICE_ADVERTISING         = 0;
    public static final int SELECT_ACTION_DEVICE_ADVERTISING_DATA    = 1;

    private static HandlerBLE mHandlerBLE;
    private static List<ServiceType> mServices;
    private static Context mContext;
    private static MyCallBack mCallBack;
    private static BluetoothDevice mDevice;
    private static String mDeviceAddress;
    private static BluetoothGatt mGatt;
    private static BluetoothAdapter mBlueAdapter = null;
    private static int mselecActionDevice;

    public static boolean isScanning = false;

    //###################################################################
    /****************** Constructor                **********************/
    //###################################################################
    public HandlerBLE(Context context)
    {
        mselecActionDevice = -1;
        mContext = context;
        mDeviceAddress= null;
        mCallBack = new MyCallBack(context, this);

    }

    //###################################################################
    /****************** Getters & setters          **********************/
    //###################################################################
    public static int getMselecActionDevice() {
        return mselecActionDevice;
    }

    public static void setMselecActionDevice(int mselecActionDevice) {
        HandlerBLE.mselecActionDevice = mselecActionDevice;
    }

    public void setDeviceAddress(String address) {
        mDeviceAddress=address;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setGatt(BluetoothGatt gatt)
    {
        mGatt = gatt;
    }

    public List<ServiceType> getServices()
    {
        return mServices;
    }

    //###################################################################
    /*********************  statics methods   **************************/
    //###################################################################
    public static HandlerBLE getInstance(Context context) {
        if(mHandlerBLE==null){
            mHandlerBLE = new HandlerBLE(context);
            setup();
        }
        return mHandlerBLE;
    }

    public static void  resetHandlerBLE()
    {
        mselecActionDevice = -1;
        mDeviceAddress     = null;
        mGatt              = null;
        if (mServices!=null) mServices.clear();
        disconnect();
    }

    public static void setup()
    {
        BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBlueAdapter = manager.getAdapter();
    }

    //###################################################################
    /********************* methods bluetooth                *************/
    //###################################################################

    public boolean IsEnabledBlue()
    {
        if(mBlueAdapter != null)
            return mBlueAdapter.isEnabled();

        return false;
    }

    public BluetoothAdapter getBlueAdapter()
    {
        return mBlueAdapter;
    }

    public void startLeScan()
    {
        try
        {
            mBlueAdapter.startLeScan(this);//deprecated
            isScanning = true;
            //mBlueAdapter.startDiscovery();
        }
        catch (Exception e)
        {
            Log.i(Constant.TAG,"(HandlerBLE)[Error]:"+e.getStackTrace()+" "+e.getCause()+" "+e.getMessage()+
                    " "+e.getLocalizedMessage());
        }

    }

    public void stopLeScan()
    {
        try
        {
            mBlueAdapter.stopLeScan(this); //deprecated
            //mBlueAdapter.startDiscovery();
            isScanning = false;
        }
        catch(Exception e)
        {
            Log.i(Constant.TAG,"(HandlerBLE)[Error]:"+e.getStackTrace()+" "+e.getCause()+" "+e.getMessage()+
                    " "+e.getLocalizedMessage());
        }
    }

    //###################################################################
    /********************* methods GATT bluetooth          *************/
    //###################################################################
    public void discoverServices() {
        if (Constant.DEBUG)
            Log.i(Constant.TAG, "(HandlerBLE)Scanning services and caracteristics");
        mGatt.discoverServices();
    }

    public void connect()
    {
        mDevice   = mBlueAdapter.getRemoteDevice(mDeviceAddress);
        mServices.clear();
        if(mGatt!=null)
        {
            mGatt.connect();
        }else
        {
            mDevice.connectGatt(mContext, false, mCallBack);
        }
    }
    public static void disconnect(){
        if (mGatt!=null) {
            try{
                mGatt.disconnect();
                mGatt.close();
                if (Constant.DEBUG)
                    Log.i(Constant.TAG, "(HandlerBLE)Disconnecting GATT");
            } catch(Exception ex){}
        }
        mGatt = null;
    }

    public boolean isConnected(){
        return (mGatt!=null);
    }

    //###################################################################
    /********************* methods Scan bluetooth          *************/
    //###################################################################
    /*
    * this method is used to receive devices which were found durind a scan*/
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
    {

        if(Constant.DEBUG)
            Log.i(Constant.TAG,"(HandlerBLE) -- onLeScan -> throwing information to the listener.");

        //create the packet wich will be sent to listener.
        Intent intent = new Intent();

        switch (mselecActionDevice)
        {
            case SELECT_ACTION_DEVICE_ADVERTISING:
                intent.setAction(HandlerBLE.ACTION_DEVICE_ADVERTISING);
                break;
            case SELECT_ACTION_DEVICE_ADVERTISING_DATA:
                intent.setAction(HandlerBLE.ACTION_DEVICE_ADVERTISING_DATA);
                break;
            default:
                intent.setAction(HandlerBLE.ACTION_DEVICE_ADVERTISING);
                break;
        }

        BeanBluetoothDevice beanBlue = new BeanBluetoothDevice();
        beanBlue.setBluetoothDevice(device);
        beanBlue.setmRssi(rssi);
        beanBlue.setmScanRecord(scanRecord);

        intent.putExtra(Constant.EXTRA_BEAN_BLUETOOTHDEVICE,beanBlue);
        mContext.sendBroadcast(intent);
    }
}
