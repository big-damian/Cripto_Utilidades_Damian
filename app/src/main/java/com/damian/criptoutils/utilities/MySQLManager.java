package com.damian.criptoutils.utilities;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

// Sacado de este video que es GOD: https://www.youtube.com/watch?v=mbHvLSwhdLA
public class MySQLManager {

//    HERE IM USING ANOTHER CLASS CALLED MySQLManagerKeys.java in com.damian.criptoutils.utilities TO STORE THE KEYS AND ASSIGN THEM INTHE LINES BELOW
//    THE CLASS LOOKS LIKE THIS:
//    CREATE THIS SAME CLASS OR ASSIGN THE KEYS DIRECTLY TO THE VARIABLES FOR THE APP TO WORK!
//
//    package com.damian.criptoutils.utilities;
//
//    public class MySQLManagerKeys {
//
//        public static String db = "my_database";
//        public static String ip = "my_ip";
//        public static String port = "my_port";
//        public static String username = "my_username";
//        public static String password = "my_password";
//
//    }

    protected static String db = MySQLManagerKeys.db;
    protected static String ip = MySQLManagerKeys.ip;
    protected static String port = MySQLManagerKeys.port;
    protected static String username = MySQLManagerKeys.username;
    protected static String password = MySQLManagerKeys.password;

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
