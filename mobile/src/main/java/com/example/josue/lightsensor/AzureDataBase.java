package com.example.josue.lightsensor;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.*;
import java.util.ArrayList;

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

    private String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String email) {
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;
        String ret = null;

        if(c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT IDUSUARIO FROM USUARIO WHERE EMAIL = '"+email+"'");
                rs.next();
                userID = rs.getString(1);
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private String ip = "travelsecurity.database.windows.net:1433";
    private String classs = "net.sourceforge.jtds.jdbc.Driver";
    private String db = "TravelSecurity";
    private String un = "ProyectoAdm@travelsecurity";
    private String password = "Proyecto0087";




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

    public String[] getUsersAndPasswords(){
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;
        ArrayList<String> ret = new ArrayList<>();

        if(c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT EMAIL,USERPASSWORD FROM USUARIO");
                while (rs.next()){
                    Log.d("usrs & pass list",rs.getString(1)+":"+rs.getString(2));
                    ret.add(rs.getString(1)+":"+rs.getString(2));

                }


                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return ret.toArray(new String[ret.size()]);

    }

    public void fillUsersDeviceList() {
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;

        if (c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT * FROM MALETA WHERE IDUSUARIO ='" + userID + "'");
                while (rs.next()) {
                    DeviceList.getInstance().add(new Device(rs.getString(1), "fromDB", false));

                }


                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
