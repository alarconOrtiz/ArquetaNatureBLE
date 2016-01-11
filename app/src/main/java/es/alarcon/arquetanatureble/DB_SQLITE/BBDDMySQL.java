package es.alarcon.arquetanatureble.DB_SQLITE;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import es.alarcon.arquetanatureble.UTIL.Constant;


/**
 * Created by alarcon on 27/7/15.
 */
public class BBDDMySQL
{

    public Connection mConnection;

    public BBDDMySQL()
    {
        super();
    }

    public boolean Connect2BDMySQL (String user, String pass, String ip, String port, String catalog)
    {
        if (mConnection == null)
        {
            String urlMySQL = "";
            if (catalog != "")
            {
                urlMySQL = "jdbc:mysql://" + ip + ":" + port + "/" + catalog;
            }else
            {
                urlMySQL = "jdbc:mysql://" + ip + ":" + port;
            }

            if (!user.isEmpty() && !ip.isEmpty() && !port.isEmpty())
            {
                try
                {
                    Class.forName("com.mysql.jdbc.Driver");//.newInstance();
                    mConnection =	DriverManager.getConnection(urlMySQL, user, pass);
                }
                catch (ClassNotFoundException e)
                {
                    if(Constant.DEBUG)
                        Log.i(Constant.TAG,"Connect2BDMySQL --" +
                                " Connect2BDMySQL -> Error: " + e.getMessage());

                    return false;
                }
                catch (SQLException e)
                {
                    if(Constant.DEBUG)
                    {
                        Log.i(Constant.TAG, "Connect2BDMySQL --" +
                                " Connect2BDMySQL -> Error: " + e.getMessage());
                        Log.i(Constant.TAG, "SQLState: " + e.getSQLState());
                        Log.i(Constant.TAG, "VendorError: " + e.getErrorCode());
                    }
                    return false;
                }
            }
        }
        return true;
    }

    //to execute query like CREATE, DROP, INSERT y UPDATE.
    public boolean ExecuteUpdate(String sqlSentence)
    {
        try
        {
            if(mConnection != null)
            {
                Statement st = mConnection.createStatement();
                st.executeUpdate(sqlSentence);
                st.close();
                return true;
            }
        }catch (SQLException eSQL)
        {
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "BBDDMySQL -- exucuteQuery -> ERROR");
            return false;
        }
        if(Constant.DEBUG)
            Log.i(Constant.TAG, "BBDDMySQL -- executeUpdate -> sql executed");
        return false;
    }

    //to execute select, this method will give back datas
    public ResultSet ExecuteQuery(String sqlSentence)
    {
        Statement st;
        ResultSet rs = null;
        try
        {
            if(mConnection != null)
            {
                 st = mConnection.createStatement();
                 rs = st.executeQuery(sqlSentence);
                 st.close();
            }
        }catch (SQLException eSQL)
        {
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "BBDDMySQL -- exucuteQuery -> ERROR");
            return null;
        }
        if(Constant.DEBUG)
            Log.i(Constant.TAG, "BBDDMySQL -- exucuteQuery -> sql executed");
        return rs;
    }

    public boolean CloseResultset(ResultSet rs)
    {
        try
        {
            if(!rs.isClosed())
               rs.close();

            if(Constant.DEBUG)
                Log.i(Constant.TAG, "BBDDMySQL -- CloseResultset -> resultset Closed");

        } catch (SQLException e)
        {
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "BBDDMySQL -- CloseResultset -> Error resultset");

            return false;
        }
        return true;
    }

    public void CloseConnection()
    {
        try {
            if(mConnection != null)
                mConnection.close();
        } catch (SQLException e) {
            if(Constant.DEBUG)
                Log.i(Constant.TAG, "BBDDMySQL -- CloseConnection -> Error closing mConnection.");
        }

    }
}
