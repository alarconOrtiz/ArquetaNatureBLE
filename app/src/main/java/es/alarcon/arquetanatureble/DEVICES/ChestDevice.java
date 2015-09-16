package es.alarcon.arquetanatureble.DEVICES;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import es.alarcon.arquetanatureble.UTIL.Constant;


/**
 * Created by alarcon on 15/7/15.
 */
public class ChestDevice extends Device
{
    private Context mContext;
    //states of ChestDevice
    public int mState = 0;

    public final static int STATE_DUMMY                = 0;
    public final static int STATE_CONNECT              = 1;
    public final static int STATE_CONNECTING           = 2;
    public final static int STATE_CONNECTED            = 3;
    public final static int STATE_RETRIEVING_SERVICES  = 4;
    public final static int STATE_SERVICES_RETRIEVED   = 5;
    public final static int STATE_DISCONNECTING        = 6;

    public ChestDevice(Context context)
    {
        super();
        mContext    = context;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);


    }
}
