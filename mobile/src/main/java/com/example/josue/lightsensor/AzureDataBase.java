package com.example.josue.lightsensor;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.*;
import com.microsoft.sqlserver.jdbc.*;

/**
 * Created by Josue on 05/04/17.
 */

public class AzureDataBase {



    //////Singleton//////////////
    protected static AzureDataBase singleton = null;

    protected AzureDataBase() {

    }

    public static AzureDataBase getInstace() {
        if (singleton == null)
            singleton = new AzureDataBase();
        return singleton;
    }
///////Singleton end///////////////

    String ip = "travelsecurity.database.windows.net:1433";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "TravelSecurity";
    String un = "ProyectoAdm@travelsecurity";
    String password = "Proyecto0087";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }

    public String executeASDF(String query){
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;
        String ret = null;

        if(c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery(query);
                rs.next();
                ret = rs.getString(2);

                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return ret;
    }
}
