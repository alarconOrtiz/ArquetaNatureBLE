package es.alarcon.arquetanatureble.BLE;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import es.alarcon.arquetanatureble.UTIL.Constant;

/**
 * Created by alarcon on 16/9/15.
 */
public class MyCallBack extends BluetoothGattCallback {

    private HandlerBLE mHandlerBLE;
    private Context mContext;

    public MyCallBack(Context mContext, HandlerBLE mHandlerBLE)
    {
        this.mContext    = mContext;
        this.mHandlerBLE = mHandlerBLE;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int newState) {
        super.onConnectionStateChange(gatt, status, newState);


        if(Constant.DEBUG){
            if (status!=0) {
                Log.i(Constant.TAG, "Error status received onConnectionStateChange: " + status + " - New state: " + newState);
            } else {
                Log.i(Constant.TAG, "onConnectionStateChange received. status = " + status +
                        " - State: " + newState);
            }
        }

        if ((status==133)||(status==257)) {
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "Unrecoverable error 133 or 257. DEVICE_DISCONNECTED intent broadcast with full reset");
            Intent intent = new Intent();
            mContext.sendBroadcast(intent);
            return;
        }

        if (newState== BluetoothProfile.STATE_CONNECTED&&status==BluetoothGatt.GATT_SUCCESS){ //Connected
            mHandlerBLE.setGatt(gatt);
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "New connected Device. DEVICE_CONNECTED intent broadcast");
            Intent intent = new Intent();
            mContext.sendBroadcast(intent);
            return;
        }

        if (newState==BluetoothProfile.STATE_DISCONNECTED&&status==BluetoothGatt.GATT_SUCCESS){ //Connected
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "Disconnected Device. DEVICE_DISCONNECTED intent broadcast");
            Intent intent = new Intent();
            mContext.sendBroadcast(intent);
            return;
        }
        if(Constant.DEBUG)
            Log.i(Constant.TAG, "Unknown values received onConnectionStateChange. Status: " + status + " - New state: " + newState);
    }


    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
    }


    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if(Constant.DEBUG){
            if(status==0){
                Log.i(Constant.TAG, "Write success ,characteristic uuid=:"+characteristic.getUuid().toString());
            }else{
                Log.i(Constant.TAG, "Write fail ,characteristic uuid=:"+characteristic.getUuid().toString()+" status="+status);
            }
        }
        Intent intent = new Intent();
        mContext.sendBroadcast(intent);
    }


    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if(Constant.DEBUG) {
            if(status==0){
                //Log.i(Constant.TAG, "Read from:"+characteristic.getUuid().toString()+" value: "+ bytesToString(characteristic.getValue()));
            } else {
                Log.i(Constant.TAG, "Read fail ,characteristic uuid=:"+characteristic.getUuid().toString()+" status="+status);
            }
        }
        Intent intent = new Intent();
        mContext.sendBroadcast(intent);
    }


    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        if(Constant.DEBUG){
            //Log.i(Constant.TAG, "NOTIFICATION onCharacteristicChanged for characteristic " + uuid +
            //		" value: " + bytesToString(characteristic.getValue()));
        }
        Intent intent = new Intent();
        mContext.sendBroadcast(intent);
    }


    @Override
    public void  onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if(Constant.DEBUG)
            Log.i(Constant.TAG, "onServicesDiscovered status: " + status);

        for(BluetoothGattService serviceInList: gatt.getServices()){
            String serviceUUID=serviceInList.getUuid().toString();
            ServiceType serviceType=new ServiceType(serviceInList);
            List<BluetoothGattCharacteristic> characteristics= serviceType.getCharacteristics();
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "New service: " + serviceUUID);
            for(BluetoothGattCharacteristic characteristicInList : serviceInList.getCharacteristics()){
                if(Constant.DEBUG)
                    Log.i(Constant.TAG, "New characteristic: " + characteristicInList.getUuid().toString());
                characteristics.add(characteristicInList);
            }
            mHandlerBLE.getServices().add(serviceType);
        }
        Intent intent = new Intent();
        mContext.sendBroadcast(intent);
    }


    @Override
    public void onDescriptorWrite(BluetoothGatt gatt,
                                  BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        if(Constant.DEBUG)
            Log.i(Constant.TAG, "onDescriptorWrite "+ descriptor.getUuid().toString() + " - characteristic: " +
                    descriptor.getCharacteristic().getUuid().toString() + " - Status: " + status);
        Intent intent = new Intent();
        mContext.sendBroadcast(intent);
    }
}
