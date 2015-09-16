package es.alarcon.arquetanatureble.GUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import es.alarcon.arquetanatureble.BLE.HandlerBLE;
import es.alarcon.arquetanatureble.BroadCastReceiver.BLEBroadcastReceiver;
import es.alarcon.arquetanatureble.CORE.BLE_Application;


import es.alarcon.arquetanatureble.DEVICES.ChestDevice;
import es.alarcon.arquetanatureble.DEVICES.Device;
import es.alarcon.arquetanatureble.R;
import es.alarcon.arquetanatureble.UTIL.Constant;


public class DeviceActivity extends Activity implements View.OnClickListener
{
    private Button cameraBt;
    private Button sendInfoBt;

    private Bitmap bmp;
    private ImageView img;
    private ImageView avatar;

    private TextView state_tv;

    private Device mDevice;
    private ChestDevice mChestDevice;
    private String mDeviceAddress;
    private String mDeviceName;

    private static BLEBroadcastReceiver mScanBroadcastReceiver;
    private static HandlerBLE mHandlerBLE;

    //###################################################################
    /****************** metodos del flujo Android. **********************/
    //###################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        img      = (ImageView) findViewById(R.id.IV_camera);
        avatar   =  (ImageView) findViewById(R.id.chamberPicture);

        avatar.setOnClickListener(this);

        //taking the id of the buttons.
        cameraBt = (Button) findViewById(R.id.bt_camera);
        sendInfoBt = (Button) findViewById(R.id.bt_sendInfo);

        cameraBt.setOnClickListener(this);
        sendInfoBt.setOnClickListener(this);

        //handler BLE
        mHandlerBLE = ((BLE_Application) getApplication()).getmHandlerBLEInstance(this);
        ((BLE_Application) getApplication()).resetHandlerBLE();
        mHandlerBLE.setMselecActionDevice(HandlerBLE.SELEC_ACTION_DEVICE_ADVERTISING_DATA);

        //Select type of device
        mDeviceAddress=getIntent().getStringExtra(Constant.EXTRA_ADDRESS);
        if (mDeviceAddress == null)
        {
            if (Constant.DEBUG)
                Log.i(Constant.TAG,"No device address received to start Activity");

            Toast.makeText(this,"Error Not device recognized ",Toast.LENGTH_LONG).show();
            finish();
        }
        mDeviceName = getIntent().getStringExtra(Constant.EXTRA_NAME);
        if(mDeviceName.toUpperCase().contains(Device.MOTE_FLOWMETER))
        {
            mDevice = new ChestDevice(this);

            if (Constant.DEBUG)
                Log.i(Constant.TAG,"device recognized : FLOWMETER");

        }
        else
        {
            if (Constant.DEBUG)
                Log.i(Constant.TAG,"No device recognized to start Activity");

            finish();
            return;
        }

        mScanBroadcastReceiver = new BLEBroadcastReceiver();
        IntentFilter i = new IntentFilter(HandlerBLE.ACTION_DEVICE_ADVERTISING_DATA);
        registerReceiver(mScanBroadcastReceiver,i);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanBroadcastReceiver);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
    //###################################################################
    /****************** metodos implements Android. *********************/
    //###################################################################
    //here events or action from usar are taken.
    @Override
    public void onClick(View v)
    {
        Intent intentActivity;
        switch (v.getId())
        {
            case R.id.bt_camera:
                intentActivity = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentActivity, 0);
                break;
            case R.id.bt_sendInfo:
                intentActivity = new Intent(this, ResumenActivity.class);
                this.startActivity(intentActivity);
                break;
            case R.id.chamberPicture:
                intentActivity= new Intent(this, ParametersDeviceActivity.class);
                this.startActivity(intentActivity);
                break;
            default:
                Log.i(Constant.TAG,"Error not ID button found it.");
                break;
        }
    }

    // this method gives us the result of picture taken with tha camera.
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        Log.i(Constant.TAG, "onActivityResult: taking the image wich has been taking with the camera");
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
        {
            Bundle ext = data.getExtras();
            bmp        = (Bitmap)ext.get("data");
            img.setImageBitmap(bmp);
        }
        else
        {
            Log.i(Constant.TAG,"onActivityResult: It couldn't possible take the image");
        }
    }

}
