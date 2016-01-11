package es.alarcon.arquetanatureble.DB_SQLITE;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import es.alarcon.arquetanatureble.BLE.AdRecord;
import es.alarcon.arquetanatureble.BEAN.BeanBluetoothDevice;
import es.alarcon.arquetanatureble.GUI.DeviceActivity;
import es.alarcon.arquetanatureble.UTIL.Constant;

/**
 * Created by alarcon on 27/7/15.
 */
public class MyAsyncTaskUpdateValueSensorDB extends AsyncTask <BeanBluetoothDevice,BeanBluetoothDevice,BeanBluetoothDevice>
{
    private static ChamberDB mChamberDB;
    private DeviceActivity mDeviceActivity;

    @Override
    protected void onPreExecute()
    {
        mChamberDB      = new ChamberDB();
    }

    @Override
    protected BeanBluetoothDevice doInBackground(BeanBluetoothDevice... params)
    {
        BeanBluetoothDevice beanBluetoothDevice;
        float valueSensor = 0;

        if(params[0] == null)
        {
            if(Constant.DEBUG)
                Log.i(Constant.TAG,"MyAsyncTaskUpdateValueSensorDB -- doInBackground() -> Error there is no data.");
            return params[0];
        }
        beanBluetoothDevice = params[0];

        //preparing the data to execute query.
        String address = beanBluetoothDevice.getBluetoothDevice().getAddress();
        byte[] scanRecord       = beanBluetoothDevice.getmScanRecord();


        List<AdRecord> listAdRecord = AdRecord.parseScanRecord(scanRecord);

        for (AdRecord record:listAdRecord)
        {
            if(record.getType() == AdRecord.TYPE_MANUFACTURER_SPECIFIC_DATA)
            {
                //byte[] bytes = record.getData();
                //valueSensor = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();

                valueSensor = 25.06f;
                if(Constant.DEBUG)
                    Log.i(Constant.TAG,"MyAsyncTaskValueSensorDB -- value sensor: "+valueSensor);
            }
        }


        if(mChamberDB.InsertDataDevice(address, valueSensor))
        {
            mChamberDB.CloseConnection();
            if(Constant.DEBUG)
                Log.i(Constant.TAG,"MyAsyncTaskUpdateValueSensorDB -- doInBackground() -> save data on DB done.");
        }
        else
        {
            if(Constant.DEBUG)
                Log.i(Constant.TAG,"MyAsyncTaskUpdateValueSensorDB -- doInBackground() -> ERROR couldn't save data on DB done.");
        }

        return null;
    }
}
