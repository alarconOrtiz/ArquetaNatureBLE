package es.alarcon.arquetanatureble.BroadCastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import es.alarcon.arquetanatureble.BEAN.AdRecord;
import es.alarcon.arquetanatureble.BEAN.BeanBluetoothDevice;
import es.alarcon.arquetanatureble.BLE.HandlerBLE;
import es.alarcon.arquetanatureble.CORE.ServiceDetectionTag;
import es.alarcon.arquetanatureble.GUI.ScanActivity;
import es.alarcon.arquetanatureble.GUI.ScanArrayAdapter;
import es.alarcon.arquetanatureble.UTIL.Constant;
import es.alarcon.arquetanatureble.DB.MyAsyncTaskUpdateValueSensorDB;


/**
 * Created by alarcon on 26/7/15.
 */
public class BLEBroadcastReceiver extends BroadcastReceiver
{
    private Activity mActivity;
    private ScanArrayAdapter mAdapter;

    public BLEBroadcastReceiver(Activity activity, ScanArrayAdapter adapter)
    {
        super();
        mAdapter  = adapter;
        mActivity = activity;
    }
    public BLEBroadcastReceiver()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(Constant.DEBUG)
            Log.i(Constant.TAG, "BLEBroadcastReceiver -- OnReceive() -> BroadcastReceiver new device found.");

        String action = intent.getAction();
        final BeanBluetoothDevice beanDeviceFound = intent.getExtras().getParcelable(Constant.EXTRA_BEAN_BLUETOOTHDEVICE);
        byte[] scanRecord = beanDeviceFound.getmScanRecord();
        //get signal and add new device into MyarrayAdapter
        if(action.equals(HandlerBLE.ACTION_DEVICE_ADVERTISING))
        {
            try {

                //mActivity = ScanActivity.GetScanActivity();
                //((ScanActivity)mActivity).addNewDevice2MySimpleArrayAdapter(beanDeviceFound);

                //final BluetoothDevice deviceFound   = beanDeviceFound.getBluetoothDevice();


                /*if(Constant.DEBUG)
                    Log.i(Constant.TAG, "BLEBroadcastReceiver -- onReceive -> ScanRecord value: "
                            +byteArray2Hexadecimal(scanRecord));*/

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        BeanBluetoothDevice oldDevice;
                        oldDevice = mAdapter.findElement(beanDeviceFound.getBluetoothDevice().getName()
                                , beanDeviceFound.getBluetoothDevice().getAddress());

                        if (oldDevice != null) {
                            //delete old one and inset new one.
                            mAdapter.deleteElement(oldDevice);
                        }

                        mAdapter.addElement(beanDeviceFound);

                        //checking the state of main activity in case Resumen o Stop send notification
                        //status bar.
                        if (!ScanActivity.isOncreate) {
                            Intent i = new Intent();
                            i.setAction(ServiceDetectionTag.ServiceBroadcastReceiver.ACTION_NOTIFY_NEW_DEVICE_FOUND);
                            mActivity.sendBroadcast(i);
                        }


                        //mAdapter.add(deviceFound);
                        //mAdapter.notifyDataSetChanged();

                        if (Constant.DEBUG)
                            Log.i(Constant.TAG, "BLEBroadcastReceiver -- OnReceive() -> Added new device "
                                    + beanDeviceFound.getBluetoothDevice().getAddress()
                                    + " --- Name: " + beanDeviceFound.getBluetoothDevice().getName());
                    }
                });
            } catch (Exception e) {
                //Log.i(Constant.TAG,"[Error(BLEBroadcastReceiver)]: \n Message: "+e.getMessage()+"\n Cause:"+e.getCause()+"\n StackTrace"+e.getStackTrace()+"\n"+e.getLocalizedMessage());
            }
        }

        //analyze data  record scand.
        if(action.equals(HandlerBLE.ACTION_DEVICE_ADVERTISING_DATA))
        {
            // to wacth the data that arrived in scandrecord.
            /*String msg = "payload = ";
            for (byte b : scanRecord)
                msg += String.format("%02x ", b);

            if(Constant.DEBUG)
                Log.i(Constant.TAG,"BLEBroadcastReceiver -> payload : " + msg);*/

            //update value on DB
            MyAsyncTaskUpdateValueSensorDB updateValueSensorDB = new MyAsyncTaskUpdateValueSensorDB();
            updateValueSensorDB.execute(beanDeviceFound);

            //update value on activity.

        }
    }








}