package com.damian.criptoutils.utilities;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

// Sacado de este video que es GOD: https://www.youtube.com/watch?v=mbHvLSwhdLA
public class MySQLManager {

//    protected static String db = "criptoutilsdamian";
//    protected static String ip = "192.168.1.145";
//    protected static String port = "3306";
//    protected static String username = "criptoutilsdamian-app";
//    protected static String password = "androideSQL";

//    protected static String db = "sql7709822";
//    protected static String ip = "sql7.freemysqlhosting.net";
//    protected static String port = "3306";
//    protected static String username = "sql7709822";
//    protected static String password = "CYKg6hIxUZ";

    protected static String db = "bxesejh9qzvvn66mxisn";
    protected static String ip = "bxesejh9qzvvn66mxisn-mysql.services.clever-cloud.com";
    protected static String port = "3306";
    protected static String username = "ujoqamr8zjksuld9";
    protected static String password = "xgWOrmU75XsNqCrZ1SDV";

    public Connection ConexionMySQL() {
        Connection cone = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            String stringConexion = "jdbc:mysql://" + ip + ":" + port + "/" + db;

            cone = DriverManager.getConnection(stringConexion, username, password);

        } catch(Exception e) {
//            throw new Exception(e);
            Log.e("MySQL", Objects.requireNonNull(e.getMessage()));
        }

        return cone;
    }

}
