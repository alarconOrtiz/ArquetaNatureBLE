package es.alarcon.arquetanatureble.DB_SQLITE;

import android.util.Log;

import java.sql.ResultSet;

import es.alarcon.arquetanatureble.UTIL.Constant;


/**
 * Created by alarcon on 27/7/15.
 */
public class ChamberDB extends BBDDMySQL
{
    //data to table db
    public static final String NAME_DATABASE             = "IoT_teleNature";
    public static final String NAME_TABLE_DEVICES        = "info_Arqueta";
    public static final String NAME_TABLE_DATA_DEVICES   = "data_sensor";

    //datas to connect with database
    public static final String IP       = "192.168.1.11";
    public static final String PORT     = "3306";
    public static final String USER     = "root";
    public static final String PASS     = "";
    public static final String CATALOG  = NAME_DATABASE;

    public ChamberDB()
    {
        super();
        Connect2BDMySQL(USER,PASS,IP,PORT,CATALOG);
    }

    public void getDeviceFromDB(String UUID)
    {
        //preparing sentence.
        String sql = "SELECT * FROM "+NAME_TABLE_DEVICES+" WHERE UUID ="+UUID+";";
        ResultSet rs = this.ExecuteQuery(sql);
        //getParametersDevice(rs);
        this.CloseResultset(rs);
    }

   /* private BeanInfoArquetaDB getParametersDevice(ResultSet rs)
    {
        return null;
    }*/

    public boolean InsertDataDevice(String address, float value)
    {
        //prepare sentence.
        String sql = "INSERT INTO "+NAME_TABLE_DATA_DEVICES+"(" +
                    "insert_time, address_arqueta, uuid_sensor,value_sensor )" +
                     "VALUES (" +
                    "now(), '"+address+"',null,"+value+");";

        if(Constant.DEBUG)
            Log.i(Constant.TAG,"ChamberDB -- InsertDataDevice()-> query = "+sql);

        if(this.ExecuteUpdate(sql))
            return this.ExecuteUpdate("commit");

        return false;
    }
}
