package es.alarcon.arquetanatureble.DEVICES;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by alarcon on 14/7/15.
 */
public class Device extends BroadcastReceiver
{
    //Define number of mote
    public static final String MOTE_FLOWMETER               = "ARQUETA"; //flowmeter

    public static final String DEVICE_CONNECTED             = "Device_connected";
    public static final String DEVICE_DISCONNECTED          = "Device_disconnected";
    public static final String EXTRA_FULL_RESET             = "extra_full_reset";
    public static final String SERVICES_DISCOVERED          = "services_discovered";


    public Device()
    {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {

    }

}
