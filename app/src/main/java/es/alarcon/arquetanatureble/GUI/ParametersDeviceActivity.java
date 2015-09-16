package es.alarcon.arquetanatureble.GUI;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import es.alarcon.arquetanatureble.BLE.HandlerBLE;
import es.alarcon.arquetanatureble.CORE.BLE_Application;
import es.alarcon.arquetanatureble.R;

public class ParametersDeviceActivity extends ActionBarActivity {

    private static HandlerBLE mHandlerBLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_device);

        //handler BLE
        mHandlerBLE = ((BLE_Application) getApplication()).getmHandlerBLEInstance(this);
        ((BLE_Application) getApplication()).resetHandlerBLE();

       // mHandlerBLE.setDeviceAddress();

       // mHandlerBLE.connect();
       // mHandlerBLE.discoverServices();

        //mHandlerBLE.setMselecActionDevice(HandlerBLE.SELEC_ACTION_DEVICE_ADVERTISING_DATA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parameters_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
