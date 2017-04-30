package com.example.josue.lightsensor;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    public boolean user_exist(String email){
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;
        String ret = null;

        if(c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT Count(*) FROM USUARIO WHERE EMAIL = '"+email+"'");
                rs.next();
                int count = rs.getInt(1);
                if (count>0){
                    return true;
                }
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean save_user(String nombre, String apellido, String email, String password){
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;
        String ret = null;

        if(c != null) {
            try {
                stmt = c.createStatement();
                String sql = "INSERT INTO USUARIO (IDUSUARIO, FIRSTNAME,FIRSTLASTNAME,USERPASSWORD,EMAIL) VALUES("+
                        "'" +email+"', " +
                        "'" +nombre+"', " +
                        "'" +apellido+"', " +
                        "'" +password+"', " +
                        "'" +email+"'"
                        +")";

                DeviceList.getInstance().newUser = 1;
                Log.d("Debug",sql);
                stmt.executeUpdate(sql);

                c.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
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
                    Device device = new Device(rs.getString(1), rs.getString("NAME"),rs.getString("MARCA"),
                            rs.getString("COLOR"),rs.getString("TAMANO"));
                    if(!DeviceList.getInstance().contains(device)){
                        DeviceList.getInstance().add(device);
                    }

                }


                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void deleteDevice(int position) {

        Connection c = CONN();
        Statement stmt;


        if (c != null) {
            try {
                stmt = c.createStatement();
                stmt.executeUpdate("DELETE FROM REGISTROMALETA WHERE IDMALETA ='" + DeviceList.getInstance().get(position).getName() + "'");
                stmt.executeUpdate("DELETE FROM HISTORIALAPERTURA WHERE IDMALETA ='" + DeviceList.getInstance().get(position).getName() + "'");
                stmt.executeUpdate("DELETE FROM MALETA WHERE IDMALETA ='" + DeviceList.getInstance().get(position).getName() + "'");
                DeviceList.getInstance().remove(position);
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public boolean addDevice(Device device) {

        Connection c = CONN();
        Statement stmt;
        if (c != null) {
            try {

                stmt = c.createStatement();
                if(!DeviceList.getInstance().contains(device)) {
                    stmt.executeUpdate("INSERT INTO MALETA (IDMALETA, IDUSUARIO, NAME, MARCA, TAMANO, COLOR) " +
                            "VALUES ('"+device.getName()+"','"+userID+"','"+device.getNameUser()+"','"
                            +device.getBrand()+"','"+device.getSize()+"','"+device.getColor()+"')");
                    DeviceList.getInstance().add(device);
                    DeviceList.getInstance().newDeviceAddedPosition = DeviceList.getInstance().indexOf(device);
                }
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public ArrayList<MarkerOptions> getMarkerOptions(String idMaleta,String days){
        ArrayList<MarkerOptions> list = new ArrayList<>();
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;

        if (c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT * FROM REGISTROMALETA WHERE IDMALETA ='"+idMaleta+"' AND FECHA BETWEEN GETDATE() - "+days+" AND GETDATE()");
                while (rs.next()) {
                    String aeropuerto = rs.getString("IDAEROPUERTO");
                    Double latitud = Double.valueOf(rs.getString("LATITUD"));
                    Double longitud = Double.valueOf(rs.getString("LONGITUD"));
                    String alarma = alarmToString(rs.getBoolean("ALARMA"));
                    Timestamp fecha = rs.getTimestamp("FECHA");


                    SimpleDateFormat toDisplay = new SimpleDateFormat("yyyy/MM/dd 'at' h:mm a 'UTC'");
                    //toDisplay.setTimeZone(TimeZone.getDefault());
                    Log.d("time",fecha.toString()+"->"+toDisplay.format(fecha)+" "+TimeZone.getDefault().toString());



                    list.add(new MarkerOptions().position(new LatLng(latitud,longitud))
                            .title(toDisplay.format(fecha))
                            .snippet(alarma));

                }


                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private String alarmToString(boolean alarma) {
        if(alarma)
            return "Alarm was activated";
        return "Alarm not activated";
    }

    public void getDeviceStatus(int i) {
        Connection c = CONN();
        Statement stmt;
        ResultSet rs;
        LatLng tempLatLong;
        Timestamp tempTime;
        Device device = DeviceList.getInstance().get(i);

        if (c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT TOP 1 * FROM REGISTROMALETA WHERE IDMALETA = '" + device.getName() + "' ORDER BY IDREGISTROMALETA DESC");
                while (rs.next()) {
                    tempLatLong = new LatLng(Double.valueOf(rs.getString(4)),Double.valueOf(rs.getString(5)));
                    Log.d("refresh device", tempLatLong.toString());
                    tempTime = rs.getTimestamp(6);
                    Log.d("refresh device", tempTime.toString());
                    Log.d("refresh device", device.getLastSeen().toString());

                    if(tempTime.after(device.getLastSeen())){
                        device.setLastSeen(tempTime);
                        device.setLatLng(tempLatLong);
                    }
                }

                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public void addAperturas(ArrayList<Apertura> listAperturas) {
        Connection c = CONN();
        Statement stmt;
        if (c != null) {
            try {

                stmt = c.createStatement();
                for(Apertura apertura : listAperturas) {
                    Log.d("insert apertura", "fecha " + apertura.getFecha().toString());

                    stmt.executeUpdate("INSERT INTO HISTORIALAPERTURA (IDMALETA, FECHA, LATITUD, LONGITUD) " +
                            "VALUES ('" + apertura.getDevice().getName() + "','" + apertura.getFecha().toString() + "','" +
                            String.valueOf(apertura.getLatLng().latitude) + "','"
                            + String.valueOf(apertura.getLatLng().longitude) + "')");
                }
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public String[] getAperturas(Integer position, Integer days) {

        Connection c = CONN();
        Statement stmt;
        ResultSet rs;
        Device device = DeviceList.getInstance().get(position);
        ArrayList<String> aux = new ArrayList<String>();


        if (c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT * FROM HISTORIALAPERTURA WHERE IDMALETA =" +
                        "'"+device.getName()+"' AND FECHA BETWEEN GETDATE() - "+days+" AND GETDATE()");
                while (rs.next()) {
                    aux.add(new SimpleDateFormat("yyyy/MM/dd 'at' h:mm a 'UTC'").format(rs.getTimestamp("FECHA")));

                }

                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        String[] stockArr = new String[aux.size()];
        stockArr = aux.toArray(stockArr);
        return stockArr;
    }
}
