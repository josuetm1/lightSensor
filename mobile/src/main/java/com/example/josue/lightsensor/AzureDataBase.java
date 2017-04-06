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

//    protected String connectionString =
//            "jdbc:sqlserver://travelsecurity.database.windows.net:1433;"
//                    + "database=TravelSecurity;"
//                    + "user=ProyectoAdm@travelsecurity;"
//                    + "password=Proyecto0087;"
//                    + "encrypt=true;"
//                    + "trustServerCertificate=false;"
//                    + "hostNameInCertificate=*.database.windows.net;"
//                    + "loginTimeout=30;";


//    public ResultSet execute(String sql) throws SQLException {
//
//
//        Connection connection = DriverManager.getConnection(connectionString);
//        Statement statement = connection.createStatement();
//        connection.createStatement();
//        ResultSet resultSet = statement.executeQuery(sql);
//        connection.commit();
//        connection.close();
//        return resultSet;
//
//    }
//
//    public void testConnect() {
//
//        // Declare the JDBC objects.
//        Connection connection = null;
//
//        try {
//
//            connection = DriverManager.getConnection(connectionString);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) try {
//                connection.close();
//            } catch (Exception e) {
//            }
//        }
//        Log.d("helooooooooooooo", "out");
//
//
//    }

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
}
